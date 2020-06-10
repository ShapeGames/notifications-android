package dk.shape.games.notifications.features.list

import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.repositories.EventNotificationsDataSource
import dk.shape.games.sportsbook.offerings.generics.event.data.EventsRepository

data class EventNotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val loadingViewProvider: ViewProvider,

    val provideNotifications: suspend () -> Notifications,

    val notificationsDataSource: EventNotificationsDataSource,

    val eventsRepository: EventsRepository,

    val eventHandler: NotificationsEventHandler,

    val provideBetEventIds: suspend () -> List<String>

)