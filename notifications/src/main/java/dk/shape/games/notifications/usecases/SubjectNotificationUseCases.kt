package dk.shape.games.notifications.usecases

import androidx.annotation.WorkerThread
import dk.shape.games.notifications.aliases.SubjectNotificationsLoadedListener
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject

internal interface SubjectNotificationUseCases {

    @WorkerThread
    suspend fun loadNotifications(
        onLoaded: SubjectNotificationsLoadedListener,
        onFailure: (error: Throwable) -> Unit = { }
    )

    @WorkerThread
    suspend fun saveNotificationPreferences(
        stateData: StateDataSubject,
        onSuccess: () -> Unit = { },
        onFailure: (error: Throwable) -> Unit = { }
    )
}