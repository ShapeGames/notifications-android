package dk.shape.games.notifications.usecases

import kotlinx.coroutines.flow.Flow

internal interface NotificationsUseCases {

    val state: Flow<NotificationsState>

    suspend fun loadNotifications()

}