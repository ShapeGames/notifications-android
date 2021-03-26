package dk.shape.games.notifications.repositories

import dk.shape.danskespil.foundation.cache.Cache
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.Flow
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription as LegacySubscription

@ExperimentalCoroutinesApi
class LegacyNotificationsRepository(
    private val service: LegacyNotificationsService,
    cache: Cache<Set<Subscription>>
) : NotificationsRepositoryBase(
    service = service,
    cache = cache
) {
    @FlowPreview
    override suspend fun getAllSubscriptions(deviceId: String): Flow<Set<Subscription>> =
        updateSubscriptionsCache(deviceId, suspend {
            service.getAllSubscriptions(deviceId).map { subscription: LegacySubscription ->
                Subscription(
                    eventId = subscription.eventId,
                    subjectId = subscription.eventId,
                    subjectType = SubjectType.EVENTS,
                    types = subscription.types.toSet()
                )
            }
        }).asFlow()

    @FlowPreview
    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> =
        updateSubscriptionsCache(deviceId, suspend {
            service.getSubscriptions(deviceId).map { subscription: LegacySubscription ->
                Subscription(
                    eventId = subscription.eventId,
                    subjectId = subscription.eventId,
                    subjectType = SubjectType.EVENTS,
                    types = subscription.types.toSet()
                )
            }
        }).asFlow()
}