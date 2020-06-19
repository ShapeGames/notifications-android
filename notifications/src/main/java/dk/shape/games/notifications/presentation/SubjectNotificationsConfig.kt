package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

/**
 * Configuration used for for configuring the BottomSheetNotifications fragment.
 *
 * @param provideDeviceId Lambda function which returns an UUID (device ID) for the device.
 *
 * @param provideNotifications Lambda function which returns a list containing notification
 * information for different sports.
 *
 * @param notificationsDataSource The data source (repository) used for interfacing with the
 * subscriptions data.
 *
 * @param eventHandler An event handler used for listening to events propagated directly from the
 * notifications component/interactor. The event handle will notifiy upon various events.
 *
 * @see SubjectNotificationsDataSource
 * @see SubjectNotificationsEventHandler
 * @see SubjectNotificationGroup
 */
data class SubjectNotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val provideNotifications: suspend () -> List<SubjectNotificationGroup>,

    val notificationsDataSource: SubjectNotificationsDataSource,

    val eventHandler: SubjectNotificationsEventHandler
)
