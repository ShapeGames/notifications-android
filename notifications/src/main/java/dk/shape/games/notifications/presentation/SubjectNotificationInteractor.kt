package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.actions.StatsNotificationsAction
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.aliases.StatsNotifications
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.features.list.StatsNotificationsEventHandler
import dk.shape.games.notifications.repositories.StatsNotificationsDataSource
import dk.shape.games.notifications.usecases.EventNotificationState
import dk.shape.games.notifications.usecases.StatsNotificationUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class SubjectNotificationInteractor (
    private val provideDeviceId: () -> String,
    private val provideNotifications: () -> StatsNotifications,
    private val coroutineScope: CoroutineScope,
    private val notificationsDataSource: StatsNotificationsDataSource,
    private val notificationsEventHandler: StatsNotificationsEventHandler
): StatsNotificationUseCases {

    override suspend fun loadNotifications(action: StatsNotificationsAction, onOntificationsLoaded: suspend (
        possibleTypes: List<StatsNotificationType>,
        activatedTypes: List<StatsNotificationType>) -> Unit
    ) {

        try {
            withContext(Dispatchers.IO) {
                notificationsDataSource.getSubscriptions(provideDeviceId()).apply {
                    collect { subscriptions ->
                        val notifications = provideNotifications()
                        val notificationsGroup = notifications.group
                            .find { it.sportId == action.sportId }
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

    override suspend fun toggleMasterForSubject(enabled: Boolean, onEnabled: suspend () -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun saveNotificationPreferences(onSaved: suspend () -> Unit) {
        TODO("Not yet implemented")
    }

    private fun setErrorState() {

    }
}