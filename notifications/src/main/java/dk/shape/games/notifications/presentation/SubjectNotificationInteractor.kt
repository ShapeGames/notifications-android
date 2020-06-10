package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.aliases.StatsNotifications
import dk.shape.games.notifications.features.list.SubjectNotificationsEventHandler
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SubjectNotificationInteractor(
    private val action: SubjectNotificationsAction,
    private val coroutineScope: CoroutineScope,
    private val provideDeviceId: () -> String,
    private val provideNotifications: () -> StatsNotifications,
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
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                notificationsEventHandler.onNotificationsLoading(true)
            }
            coroutineScope.launch(Dispatchers.IO) {
                notificationsDataSource.getSubscriptions(provideDeviceId()).apply {
                    collect { subscriptions ->

                        val notifications = provideNotifications()
                        val notificationsGroup =
                            notifications.group.find { it.sportId == action.sportId }

                        if (notificationsGroup != null) {
                            val subscription =
                                subscriptions.find { it.subjectId == action.subjectId }

                            val enabledNotificationTypes =
                                subscription?.types?.mapNotNull { subscriptionType ->
                                    notificationsGroup.notificationTypes.find { notificationType ->
                                        notificationType.identifier.name.toLowerCase(Locale.getDefault()) == subscriptionType
                                    }
                                }?.toSet() ?: emptySet()

                            withContext(Dispatchers.Main) {
                                if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                                    notificationsEventHandler.onNotificationsLoading(false)
                                }
                                onLoaded(
                                    enabledNotificationTypes,
                                    notificationsGroup.notificationTypes,
                                    notificationsGroup.defaultNotificationTypeIdentifiers.toSet()
                                )
                            }

                        } else throw IllegalArgumentException("Subject ${action.subjectId} is missing default notifications")
                    }
                }
            }
        } catch (e: Exception) {
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                notificationsEventHandler.onNotificationsLoading(false)
                notificationsEventHandler.onPreferencesLoadedError(e)
            }
            onFailure(e)
        }
    }

    override fun saveNotificationPreferences(
        stateData: SubjectNotificationStateData,
        onSuccess: () -> Unit,
        onFailure: (error: Throwable) -> Unit
    ) {
        try {
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                notificationsEventHandler.onNotificationsLoading(true)
            }
            coroutineScope.launch(Dispatchers.IO) {
                notificationsDataSource.updateSubjectSubscriptions(
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
                    }
                    if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                        notificationsEventHandler.onNotificationsLoading(false)
                    }
                    onSuccess()
                }
            }
        } catch (e: Exception) {
            onFailure(e)
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full) {
                notificationsEventHandler.onNotificationsLoading(false)
                notificationsEventHandler.onPreferencesSavedError(e)
            }
        }
    }
}