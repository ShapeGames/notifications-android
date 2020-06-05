package dk.shape.games.notifications.usecases

import kotlinx.coroutines.flow.Flow

internal interface NotificationTypeUseCases {

    val state: Flow<NotificationTypeState>

    suspend fun loadNotificationType()

}