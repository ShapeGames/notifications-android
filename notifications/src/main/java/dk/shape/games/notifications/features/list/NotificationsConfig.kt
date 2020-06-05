package dk.shape.games.notifications.features.list

import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.repositories.NotificationsDataSource
import dk.shape.games.sportsbook.offerings.generics.event.data.EventsRepository

data class NotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val loadingViewProvider: ViewProvider,

    val provideNotifications: suspend () -> Notifications,

    val notificationsDataSource: NotificationsDataSource,

    val eventsRepository: EventsRepository,

    val eventHandler: NotificationsEventHandler,

    val provideBetEventIds: suspend () -> List<String>

)