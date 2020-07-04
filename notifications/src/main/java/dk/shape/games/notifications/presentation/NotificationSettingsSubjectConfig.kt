package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

// TODO: add description once API is stable

data class NotificationSettingsSubjectConfig(

    val provideDeviceId: suspend () -> String,

    val notificationsDataSource: SubjectNotificationsDataSource
)
