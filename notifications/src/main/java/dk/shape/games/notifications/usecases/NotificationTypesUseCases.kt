package dk.shape.games.notifications.usecases

import kotlinx.coroutines.flow.Flow

internal interface NotificationTypesUseCases {

    val state: Flow<NotificationTypesState>

    suspend fun loadNotificationTypes()

    suspend fun toggleNotificationType(notificationTypeId: String, enabled: Boolean)

}