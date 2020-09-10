package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.LegacyNotificationGroup
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface

/**
 * add description when API is stable
 */
data class EventNotificationsSheetConfig(

    val provideNotifications: suspend () -> List<LegacyNotificationGroup>,

    val provideNotificationsNow: () -> List<LegacyNotificationGroup>?,

    val notificationsDataSource: NotificationsComponentInterface,

    val eventHandler: EventNotificationsSheetEventHandler
)
