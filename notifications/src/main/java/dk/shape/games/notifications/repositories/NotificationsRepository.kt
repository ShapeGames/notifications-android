package dk.shape.games.notifications.repositories

import dk.shape.danskespil.foundation.cache.Cache
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class NotificationsRepository(
    private val service: NotificationsService,
    private val cache: Cache<Set<Subscription>>
) : EventNotificationsDataSource, SubjectNotificationsDataSource {

    private val updateMutexes = ConcurrentHashMap<String, Mutex>()
    private val cacheMutex = Mutex(false)

    @ExperimentalCoroutinesApi
    private val subscriptionsChannels =
        ConcurrentHashMap<String, BroadcastChannel<Set<Subscription>>>()

    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> =
        updateSubscriptionsCache(deviceId).asFlow()

    private suspend fun updateSubscriptionsCache(deviceId: String): BroadcastChannel<Set<Subscription>> {
        val subscriptionsChannel = getChannelForDeviceId(deviceId)
        val cachedSubscriptions = cache.get(deviceId)
        if (cachedSubscriptions == null) {
            val subscriptions = service.getSubscriptions(deviceId)
                .toSet()
                .sortSubscriptions()

            cacheMutex.withLock {
                cache.put(deviceId, subscriptions, Cache.CacheDuration.Infinite)
            }
            subscriptionsChannel.sendBlocking(subscriptions)
        } else {
            subscriptionsChannel.sendBlocking(cachedSubscriptions)
        }
        return subscriptionsChannel
    }

    override suspend fun updateSubjectSubscriptions(
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    ) {
        updateMutexes.getOrPut("$subjectId|${subjectType.name}", { Mutex(false) }).withLock {
            service.updateSubscriptions(
                SubscribeRequest(
                    deviceId = subjectId,
                    eventId = subjectId,
                    subjectId = subjectId,
                    subjetType = subjectType,
                    types = subscribedNotificationTypeIds.toList()
                )
            )
        }
        cacheMutex.withLock {
            // We want to update the cache only if other subscriptions were already fetched, which
            // means we have the device ID in the cache.
            cache.get(subjectId)?.let {
                val cachedSubscriptions = (it.filter { subscription ->
                    subscription.subjectId != subjectId
                }.toSet() + Subscription(
                    eventId = subjectId,
                    subjectId = subjectId,
                    subjectType = subjectType,
                    types = subscribedNotificationTypeIds
                )).sortSubscriptions()

                cache.put(subjectId, cachedSubscriptions, Cache.CacheDuration.Infinite)
                getChannelForDeviceId(subjectId).sendBlocking(cachedSubscriptions)
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
                    subjectId = eventId,
                    subjetType = SubjectType.EVENTS,
                    types = subscribedNotificationTypeIds.toList()
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
                )).sortSubscriptions()

                cache.put(deviceId, cachedSubscriptions, Cache.CacheDuration.Infinite)
                getChannelForDeviceId(deviceId).sendBlocking(cachedSubscriptions)
            }
        }
    }

    private fun getChannelForDeviceId(deviceId: String) = subscriptionsChannels.getOrPut(deviceId, {
        BroadcastChannel(Channel.CONFLATED)
    })

    private fun Set<Subscription>.sortSubscriptions(): SortedSet<Subscription> =
        this.toSortedSet(
            kotlin.Comparator { s1, s2 ->
                if (s1.eventId != null && s2.eventId != null) {
                    s1.eventId.toLong().compareTo(s2.eventId.toLong())
                } else 0
            }
        )

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