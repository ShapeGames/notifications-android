package dk.shape.games.notifications.repositories

import dk.shape.danskespil.foundation.cache.Cache
import dk.shape.games.notifications.entities.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

@ExperimentalCoroutinesApi
class NotificationsRepository(
    private val service: NotificationsService,
    cache: Cache<Set<Subscription>>
) : NotificationsRepositoryBase(
    service = service,
    cache = cache
) {
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
}