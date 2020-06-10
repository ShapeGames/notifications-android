package dk.shape.games.notifications.repositories

import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.coroutines.flow.Flow

interface NotificationsDataSource {

    suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>>

    suspend fun register(
        deviceId: String,
        platform: String,
        environment: String,
        notificationToken: String
    )

    fun isUpdatingSubscriptions(deviceId: String): Boolean
}

interface EventNotificationsDataSource: NotificationsDataSource {

    suspend fun updateEventSubscriptions(
        deviceId: String,
        eventId: String,
        subscribedNotificationTypeIds: Set<String>
    )
}

interface StatsNotificationsDataSource: NotificationsDataSource {

    suspend fun updateEventSubscriptions(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    )
}