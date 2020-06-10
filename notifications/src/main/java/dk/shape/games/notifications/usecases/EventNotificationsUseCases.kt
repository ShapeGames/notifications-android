package dk.shape.games.notifications.usecases

import kotlinx.coroutines.flow.Flow

internal interface EventNotificationsUseCases {

    val state: Flow<EventNotificationsState>

    suspend fun loadNotifications()

}