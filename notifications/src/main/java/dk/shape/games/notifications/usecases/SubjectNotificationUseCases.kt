package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.aliases.NotifificationsLoadedListener
import dk.shape.games.notifications.presentation.SubjectNotificationStateData


internal interface SubjectNotificationUseCases {

    suspend fun loadNotifications(
        onLoaded: NotifificationsLoadedListener, onFailure: (error: Throwable) -> Unit  = { }
    )

    fun saveNotificationPreferences(
        stateData: SubjectNotificationStateData,
        onSuccess: () -> Unit = { },
        onFailure: (error: Throwable) -> Unit = { }
    )
}