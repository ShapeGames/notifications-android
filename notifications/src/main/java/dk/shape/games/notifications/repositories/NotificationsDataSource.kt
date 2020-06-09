package dk.shape.games.notifications.repositories

import dk.shape.games.notifications.entities.Subscription
import kotlinx.coroutines.flow.Flow

interface NotificationsDataSource {

    suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>>

    suspend fun updateEventSubscriptions(
        deviceId: String,
        eventId: String,
        subscribedNotificationTypeIds: Set<String>
    )

    suspend fun register(
        deviceId: String,
        platform: String,
        environment: String,
        notificationToken: String
    )

    fun isUpdatingSubscriptions(deviceId: String): Boolean

}