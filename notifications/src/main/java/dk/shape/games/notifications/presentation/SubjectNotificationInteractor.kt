package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.aliases.StatsNotifications
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.util.*

class SubjectNotificationInteractor(
    private val action: SubjectNotificationsAction,
    private val provideDeviceId: suspend () -> String,
    private val provideNotifications: suspend () -> StatsNotifications,
    private val notificationsDataSource: SubjectNotificationsDataSource,
    private val notificationsEventHandler: SubjectNotificationsEventHandler
) : SubjectNotificationUseCases {

    override suspend fun loadNotifications(
        onLoaded: (
            activatedTypes: Set<StatsNotificationType>,
            possibleTypes: List<StatsNotificationType>,
            defaultTypes: Set<StatsNotificationIdentifier>
        ) -> Unit,
        onFailure: (error: Throwable) -> Unit
    ) {
        try {
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                notificationsEventHandler.onNotificationsLoading(true)
            }

            notificationsDataSource.getSubscriptions(provideDeviceId()).apply {
                collect { subscriptions ->
                    val notifications = provideNotifications()
                    val notificationsGroup =
                        notifications.group.find { it.sportId == action.sportId }

                    if (notificationsGroup != null) {
                        val subscription =
                            subscriptions.find { it.subjectId == action.subjectId }

                        subscription?.let {
                            val enabledNotificationTypes =
                                subscription.types.mapNotNull { subscriptionType ->
                                    notificationsGroup.notificationTypes.find { notificationType ->
                                        notificationType.identifier.name.toLowerCase(Locale.getDefault()) == subscriptionType
                                    }
                                }.toSet()

                            val defaultIdentifiers = if (enabledNotificationTypes.isEmpty()) {
                                notificationsGroup.defaultNotificationTypeIdentifiers.toSet()
                            } else emptySet()

                            withContext(Dispatchers.Main) {
                                if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                                    notificationsEventHandler.onNotificationsLoading(false)
                                }
                                onLoaded(
                                    enabledNotificationTypes,
                                    notificationsGroup.notificationTypes,
                                    defaultIdentifiers
                                )
                            }
                        }
                    } else throw IllegalArgumentException("Subject ${action.subjectId} is missing default notifications")
                }
            }
        } catch (e: Exception) {
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                notificationsEventHandler.onNotificationsLoading(false)
                notificationsEventHandler.onPreferencesLoadedError(e)
            }
            onFailure(e)
        }
    }

    override suspend fun saveNotificationPreferences(
        stateData: SubjectNotificationStateData,
        onSuccess: () -> Unit,
        onFailure: (error: Throwable) -> Unit
    ) {
        try {
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                notificationsEventHandler.onNotificationsLoading(true)
            }

            notificationsDataSource.updateSubjectSubscriptions(
                deviceId = provideDeviceId(),
                subjectId = action.subjectId,
                subjectType = action.subjectType,
                subscribedNotificationTypeIds = stateData.notificationTypeIdentifiers.map {
                    it.name.toLowerCase(Locale.getDefault())
                }.toSet()
            )

            withContext(Dispatchers.Main) {
                if (stateData.notificationTypeIdentifiers.isNotEmpty()) {
                    if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                        notificationsEventHandler.onNotificationsActivated(
                            subjectId = stateData.subjectId,
                            subjectType = stateData.subjectType
                        )
                    }
                } else {
                    if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                        notificationsEventHandler.onNotificationsDeactivated(
                            subjectId = stateData.subjectId,
                            subjectType = stateData.subjectType
                        )
                    }
                }
                if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                    notificationsEventHandler.onNotificationsLoading(false)
                }
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure(e)
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                notificationsEventHandler.onNotificationsLoading(false)
                notificationsEventHandler.onPreferencesSavedError(e)
            }
        }
    }
}