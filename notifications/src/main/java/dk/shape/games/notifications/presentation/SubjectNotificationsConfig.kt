package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.StatsNotifications
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

data class SubjectNotificationsConfig(

    val screenTitle: () -> String,

    val provideDeviceId: suspend () -> String,

    val provideNotifications: suspend () -> StatsNotifications,

    val notificationsDataSource: SubjectNotificationsDataSource,

    val eventHandler: SubjectNotificationsEventHandler
)