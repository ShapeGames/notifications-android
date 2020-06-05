package dk.shape.games.notifications.usecases

import dk.shape.componentkit.bridge.coroutines.await
import dk.shape.componentkit2.ComponentKit
import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.repositories.NotificationsDataSource
import dk.shape.games.sportsbook.offerings.generics.event.data.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

internal class NotificationTypeInteractor(
    private val eventId: String,
    private val notificationTypeId: String,
    private val provideNotifications: suspend () -> Notifications,
    private val provideDeviceId: suspend () -> String,
    private val eventRepository: EventRepository,
    private val notificationsDataSource: NotificationsDataSource
) : NotificationTypeUseCases {

    private val mutableState: BroadcastChannel<NotificationTypeState> =
        BroadcastChannel(Channel.CONFLATED)
    override val state: Flow<NotificationTypeState> = mutableState.asFlow()

    override suspend fun loadNotificationType() {
        withContext(Dispatchers.IO) {
            val deviceId = provideDeviceId()
            notificationsDataSource.getSubscriptions(deviceId).apply {
                collect { subscriptions ->
                    if(!notificationsDataSource.isUpdatingSubscriptions(deviceId)) {
                        val event = eventRepository.getData(eventId)
                            .await(ComponentKit.createBackgroundExecutor())
                        val eventNotificationTypes = provideNotifications().group
                            .find { it.groupId == event.notificationConfigurationId }
                            ?.notificationTypes
                        val currentlyEnabledNotificationTypes =
                            subscriptions
                                .find { it.eventId == eventId }
                                ?.types?.mapNotNull { type -> eventNotificationTypes?.find { it.identifier == type } }?.toSet()
                                ?: emptySet()
                        val notificationType = eventNotificationTypes?.find {
                            it.identifier == notificationTypeId
                        }
                        val enabled = currentlyEnabledNotificationTypes.find {
                            it.identifier == notificationTypeId
                        } != null
                        mutableState.sendBlocking(
                            NotificationTypeState.Content(
                                notificationTypeId,
                                notificationType?.name,
                                enabled
                            )
                        )
                    }
                }
                notificationsDataSource.getSubscriptions(provideDeviceId())
            }
        }
    }

}