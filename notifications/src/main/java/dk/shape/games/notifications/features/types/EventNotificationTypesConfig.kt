package dk.shape.games.notifications.features.types

import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.repositories.NotificationsDataSource
import dk.shape.games.sportsbook.offerings.generics.event.data.EventRepository

data class EventNotificationTypesConfig(

    val provideDeviceId: suspend () -> String,

    val provideNotifications: suspend () -> Notifications,

    val notificationsDataSource: NotificationsDataSource,

    val eventHandler: NotificationTypesEventHandler,

    val loadingViewProvider: ViewProvider,

    val eventRepository: EventRepository

)