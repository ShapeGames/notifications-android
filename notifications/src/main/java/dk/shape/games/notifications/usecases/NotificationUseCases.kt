package dk.shape.games.notifications.usecases

import kotlinx.coroutines.flow.Flow

internal interface NotificationUseCases {

    val state: Flow<EventNotificationState>

    suspend fun loadNotification()

    suspend fun toggle(enabled: Boolean)
    
}