package dk.shape.games.notifications.usecases

import kotlinx.coroutines.flow.Flow

internal interface EventNotificationTypesUseCases {

    val state: Flow<EventNotificationTypesState>

    suspend fun loadNotificationTypes()

    suspend fun toggleNotificationType(notificationTypeId: String, enabled: Boolean)

}