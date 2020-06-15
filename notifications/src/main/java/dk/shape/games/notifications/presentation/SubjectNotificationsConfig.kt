package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.SubjectNotifications
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

data class SubjectNotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val provideNotifications: suspend () -> SubjectNotifications,

    val notificationsDataSource: SubjectNotificationsDataSource,

    val eventHandler: SubjectNotificationsEventHandler
)