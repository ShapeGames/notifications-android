package dk.shape.games.notifications.repositories

import dk.shape.danskespil.foundation.cache.Cache
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

@ExperimentalCoroutinesApi
class NotificationsRepository(
    private val service: NotificationsService,
    private val cache: Cache<Set<Subscription>>
) : EventNotificationsDataSource, SubjectNotificationsDataSource {

    private val updateMutexes = ConcurrentHashMap<String, Mutex>()
    private val cacheMutex = Mutex(false)

    @ExperimentalCoroutinesApi
    private val subscriptionsChannels =
        ConcurrentHashMap<String, BroadcastChannel<Set<Subscription>>>()

    @FlowPreview
    override suspend fun getAllSubscriptions(deviceId: String): Flow<Set<Subscription>> =
        updateSubscriptionsCache(deviceId, suspend {
            service.getAllSubscriptions(deviceId)
        }).asFlow()

    @FlowPreview
    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> =
        updateSubscriptionsCache(deviceId, suspend {
            service.getSubscriptions(deviceId)
        }).asFlow()

    private suspend fun updateSubscriptionsCache(
        deviceId: String,
        subscriptionsProvider: suspend () -> List<Subscription>
    ): BroadcastChannel<Set<Subscription>> {
        val subscriptionsChannel = getChannelForDeviceId(deviceId)
        val cachedSubscriptions = cache.get(deviceId)
        if (cachedSubscriptions == null) {
            val subscriptions = subscriptionsProvider().toSet()

            cacheMutex.withLock {
                cache.put(deviceId, subscriptions, Cache.CacheDuration.Infinite)
            }
            subscriptionsChannel.sendBlocking(subscriptions)
        } else {
            subscriptionsChannel.sendBlocking(cachedSubscriptions)
        }
        return subscriptionsChannel
    }

    @FlowPreview
    override suspend fun hasActiveEventSubscription(
        deviceId: String,
        eventId: String
    ): Flow<Boolean> {
        return getAllSubscriptions(deviceId).map { subscriptions ->
            val subscription = subscriptions.find { it.eventId == eventId }
            subscription != null && subscription.types.isNotEmpty()
        }
    }

    @FlowPreview
    override suspend fun hasActiveSubjectSubscription(
        deviceId: String,
        subjectId: String
    ): Flow<Boolean> {
        return getAllSubscriptions(deviceId).map { subscriptions ->
            val subscription = subscriptions.find { it.subjectId == subjectId }
            subscription != null && subscription.types.isNotEmpty()
        }
    }

    override suspend fun updateSubjectSubscriptions(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    ) {
        updateMutexes.getOrPut("$deviceId|$subjectId", { Mutex(false) }).withLock {
            service.updateSubscriptions(
                SubscribeRequest(
                    deviceId = deviceId,
                    subjectId = subjectId,
                    subjetType = subjectType,
                    types = subscribedNotificationTypeIds
                )
            )
        }
        cacheMutex.withLock {
            // We want to update the cache only if other subscriptions were already fetched, which
            // means we have the device ID in the cache.
            cache.get(deviceId)?.let {
                val cachedSubscriptions = (it.filter { subscription ->
                    subscription.subjectId != subjectId
                }.toSet() + Subscription(
                    subjectId = subjectId,
                    subjectType = subjectType,
                    types = subscribedNotificationTypeIds
                ))

                cache.put(deviceId, cachedSubscriptions, Cache.CacheDuration.Infinite)
                getChannelForDeviceId(deviceId).sendBlocking(cachedSubscriptions)
            }
        }
    }

    override suspend fun updateEventSubscriptions(
        deviceId: String,
        eventId: String,
        subscribedNotificationTypeIds: Set<String>
    ) {
        updateMutexes.getOrPut("$deviceId|$eventId", { Mutex(false) }).withLock {
            service.updateSubscriptions(
                SubscribeRequest(
                    deviceId = deviceId,
                    eventId = eventId,
                    types = subscribedNotificationTypeIds
                )
            )
        }
        cacheMutex.withLock {
            // We want to update the cache only if other subscriptions were already fetched, which
            // means we have the device ID in the cache.
            cache.get(deviceId)?.let {
                val cachedSubscriptions = (it.filter { subscription ->
                    subscription.eventId != eventId
                }.toSet() + Subscription(
                    eventId = eventId,
                    subjectId = eventId,
                    subjectType = SubjectType.EVENTS,
                    types = subscribedNotificationTypeIds
                ))

                cache.put(deviceId, cachedSubscriptions, Cache.CacheDuration.Infinite)
                getChannelForDeviceId(deviceId).sendBlocking(cachedSubscriptions)
            }
        }
    }

    private fun getChannelForDeviceId(deviceId: String) = subscriptionsChannels.getOrPut(deviceId, {
        BroadcastChannel(Channel.CONFLATED)
    })

    override suspend fun register(
        deviceId: String,
        platform: String,
        environment: String,
        notificationToken: String
    ) {
        service.register(
            RegistrationRequest(deviceId, platform, environment, notificationToken)
        )
    }

    override fun isUpdatingSubscriptions(
        deviceId: String
    ): Boolean {
        return updateMutexes.entries.find {
            it.key.startsWith(deviceId) && it.value.isLocked
        } != null
    }
}