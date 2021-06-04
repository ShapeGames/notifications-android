package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.aliases.LegacyNotificationType
import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.repositories.EventNotificationsDataSource
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

internal class EventNotificationInteractor(
    private val event: Event,
    private val provideNotifications: suspend () -> Notifications,
    private val notificationsDataSource: EventNotificationsDataSource,
    private val provideDeviceId: suspend () -> String,
    private val onMainToggleError: (e: Throwable) -> Unit
) : EventNotificationUseCases {

    private val mutableState: BroadcastChannel<EventNotificationState> =
        BroadcastChannel(Channel.CONFLATED)
    override val state: Flow<EventNotificationState> = mutableState.asFlow()

    private var lastKnownNotificationTypes: Set<LegacyNotificationType>? = null

    override suspend fun loadNotification() {
        try {
            withContext(Dispatchers.IO) {
                notificationsDataSource.getSubscriptions(provideDeviceId()).apply {
                    collect { subscriptions ->
                        val notifications = provideNotifications()
                        val notificationsGroup = notifications.group
                            .find { it.groupId == event.notificationConfigurationId }
                        if (notificationsGroup != null) {
                            val subscription = subscriptions
                                .find { it.eventId == event.id }

                            val enabledNotificationTypes =
                                subscription?.types?.mapNotNull { subscriptionType ->
                                    notificationsGroup.notificationTypes.find { notificationType ->
                                        notificationType.identifier == subscriptionType
                                    }
                                }?.toSet() ?: emptySet()

                            lastKnownNotificationTypes = enabledNotificationTypes
                            mutableState.sendBlocking(
                                EventNotificationState.Content.create(
                                    event,
                                    enabledNotificationTypes
                                )
                            )
                        } else throw IllegalArgumentException("Event " + event.id + " is missing default notifications")
                    }
                }
            }
        } catch (e: Exception) {
            setErrorState()
        }
    }

    override suspend fun toggle(enabled: Boolean) = withContext(Dispatchers.IO) {
        try {
            withContext(Dispatchers.IO) {
                val newNotificationTypes = if (enabled) {
                    val notifications = provideNotifications()
                    val notificationsGroup = notifications.group
                        .find { it.groupId == event.notificationConfigurationId }
                    notificationsGroup?.defaultNotificationTypeIdentifiers?.mapNotNull {
                        notificationsGroup.notificationTypes.find { type ->
                            type.identifier == it
                        }
                    }?.toSet() ?: emptySet()
                } else emptySet()

                notificationsDataSource.updateEventSubscriptions(
                    provideDeviceId(),
                    event.id,
                    newNotificationTypes.map { it.identifier }.toSet()
                )

                lastKnownNotificationTypes = newNotificationTypes
                mutableState.sendBlocking(
                    EventNotificationState.Content.create(
                        event,
                        newNotificationTypes
                    )
                )
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onMainToggleError(e)
            }
            setErrorState()
        }
    }

    private suspend fun setErrorState() {
        mutableState.sendBlocking(
            EventNotificationState.Error(
                EventNotificationState.Content.create(
                    event,
                    lastKnownNotificationTypes ?: emptySet()
                )
            )
        )
    }

}