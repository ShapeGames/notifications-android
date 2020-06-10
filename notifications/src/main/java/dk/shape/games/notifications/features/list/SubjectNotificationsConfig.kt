package dk.shape.games.notifications.features.list

import dk.shape.games.notifications.aliases.StatsNotifications
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

data class SubjectNotificationsConfig(

    val screenTitle: () -> String,

    val provideDeviceId: () -> String,

    val provideNotifications: () -> StatsNotifications,

    val notificationsDataSource: SubjectNotificationsDataSource,

    val eventHandler: SubjectNotificationsEventHandler
)