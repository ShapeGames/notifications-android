package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource

/**
 * Configuration used for for configuring the BottomSheetNotifications fragment.
 *
 * @param provideDeviceId Lambda function which returns an UUID (device ID) for the device.
 *
 * @param provideNotifications Lambda function which returns a list containing notification
 * information for differnt sports.
 *
 * @param hasCachedConfigData Lambda function which returns a boolean indicating whether or not
 * the app configuration data from which the notifications data is retrieved is currrently cahced
 * and readily available. If the data is must be fetch this should return false.
 *
 * @param notificationsDataSource The data source (repository) used for interfacing with the
 * subscriptions data.
 *
 * @param eventHandler An event handler used for listening to events propagated directly from the
 * noticications component/interactor. The event handle will notifiy upon various events.
 *
 * @see SubjectNotificationsDataSource
 * @see SubjectNotificationsEventHandler
 * @see SubjectNotificationGroup
 */
data class SubjectNotificationsConfig(

    val provideDeviceId: suspend () -> String,

    val provideNotifications: suspend () -> List<SubjectNotificationGroup>,

    val hasCachedConfigData: () -> Boolean,

    val notificationsDataSource: SubjectNotificationsDataSource,

    val eventHandler: SubjectNotificationsEventHandler
)