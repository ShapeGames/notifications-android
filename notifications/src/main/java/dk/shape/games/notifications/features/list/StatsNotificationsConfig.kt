package dk.shape.games.notifications.features.list

import dk.shape.games.notifications.aliases.StatsNotifications
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.repositories.StatsNotificationsDataSource

data class StatsNotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val loadingViewProvider: ViewProvider,

    val provideNotifications: () -> StatsNotifications,

    val notificationsDataSource: StatsNotificationsDataSource,

    val eventHandler: StatsNotificationsEventHandler
)