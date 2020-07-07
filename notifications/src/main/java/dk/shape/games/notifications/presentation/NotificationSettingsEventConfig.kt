package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.presentation.viewmodels.state.StateDataEvent
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface

// TODO: add description once API is stable

data class NotificationSettingsEventConfig(

    val notificationsDataSource: NotificationsComponentInterface,

    val provideDeviceId: suspend () -> String,

    val eventListener: NotificationSettingsEventEventListener
)

interface NotificationSettingsEventEventListener {

    val onNotificationTypesChanged: (StateDataEvent) -> Unit

    val onDismiss: () -> Unit
}
