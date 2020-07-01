package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription

interface SubjectSettingsNotificationsUseCases {

    suspend fun getAllSubscriptions(
        deviceId: String
    ): Set<Subscription>

    suspend fun updateNotifications(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    )
}