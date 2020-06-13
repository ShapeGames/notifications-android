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
}

interface EventNotificationsDataSource: NotificationsDataSource {

    suspend fun updateEventSubscriptions(
        deviceId: String,
        eventId: String,
        subscribedNotificationTypeIds: Set<String>
    )

    fun isUpdatingSubscriptions(deviceId: String): Boolean
}

interface SubjectNotificationsDataSource: NotificationsDataSource {

    suspend fun hasActiveSubscription(
        deviceId: String,
        subjectId: String
    ): Flow<Boolean>

    suspend fun updateSubjectSubscriptions(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    )
}