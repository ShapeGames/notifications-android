package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

// TODO: add description once API is stable

data class NotificationSettingsSubjectConfig(

    val notificationsDataSource: SubjectNotificationsDataSource,

    val provideDeviceId: suspend () -> String,

    val eventListener: NotificationSettingsSubjectEventListener
)

interface NotificationSettingsSubjectEventListener {

    val onNotificationTypesChanged: (StateDataSubject) -> Unit

    val onDismiss: () -> Unit
}
