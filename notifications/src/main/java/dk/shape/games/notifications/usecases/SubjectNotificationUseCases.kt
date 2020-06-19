package dk.shape.games.notifications.usecases

import androidx.annotation.WorkerThread
import dk.shape.games.notifications.aliases.NotificationsLoadedListener
import dk.shape.games.notifications.presentation.SubjectNotificationStateData

internal interface SubjectNotificationUseCases {

    @WorkerThread
    suspend fun loadNotifications(
        onLoaded: NotificationsLoadedListener,
        onFailure: (error: Throwable) -> Unit = { }
    )

    @WorkerThread
    suspend fun saveNotificationPreferences(
        stateData: SubjectNotificationStateData,
        onSuccess: () -> Unit = { },
        onFailure: (error: Throwable) -> Unit = { }
    )
}