package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

data class SubjectNotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val provideNotifications: suspend () -> List<SubjectNotificationGroup>,

    val hasCachedConfigData: () -> Boolean,

    val notificationsDataSource: SubjectNotificationsDataSource,

    val eventHandler: SubjectNotificationsEventHandler
)