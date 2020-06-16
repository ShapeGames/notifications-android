package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.NotifificationsLoadedListener
import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class SubjectNotificationsInteractor(
    private val action: SubjectNotificationsAction,
    private val provideDeviceId: suspend () -> String,
    private val notificationsProvider: suspend () -> List<SubjectNotificationGroup>,
    private val notificationsDataSource: SubjectNotificationsDataSource,
    private val notificationsEventHandler: SubjectNotificationsEventHandler
) : SubjectNotificationUseCases {

    override suspend fun loadNotifications(
        onLoaded: NotifificationsLoadedListener,
        onFailure: (error: Throwable) -> Unit
    ) {
        if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
            notificationsEventHandler.onNotificationsLoading(true)
        }

        try {
            notificationsDataSource.getSubscriptions(provideDeviceId()).apply {
                collect { subscriptions ->
                    val notifications = notificationsProvider()
                    val notificationsGroup =
                        notifications.find { it.sportId == action.sportId }

                    if (notificationsGroup != null) {
                        val subscription =
                            subscriptions.find { it.subjectId == action.subjectId }

                        val enabledNotificationTypes =
                            subscription?.types?.mapNotNull { subscriptionType ->
                                notificationsGroup.notificationTypes.find { notificationType ->
                                    notificationType.identifier.name.toLowerCase(Locale.ROOT) == subscriptionType
                                }
                            }?.toSet() ?: emptySet()

                        val defaultIdentifiers = if (enabledNotificationTypes.isEmpty()) {
                            notificationsGroup.defaultNotificationTypeIdentifiers.toSet()
                        } else emptySet()

                        val possibleNotificationTypes =
                            notificationsGroup.notificationTypes.toSet()

                        withContext(Dispatchers.Main) {
                            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                                notificationsEventHandler.onNotificationsLoading(false)
                            }
                            onLoaded(
                                enabledNotificationTypes,
                                possibleNotificationTypes,
                                defaultIdentifiers
                            )
                        }
                    } else {
                        val message = "Sport: ${action.sportId} is missing subject notifications"
                        onFailure(IllegalArgumentException(message))
                    }
                }
            }
        } catch (e: Exception) {
            if (e is IOException || e is HttpException) {
                if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                    notificationsEventHandler.onNotificationsLoading(false)
                    notificationsEventHandler.onPreferencesLoadedError(e)
                }
                onFailure(e)
            } else throw e
        }
    }

    override suspend fun saveNotificationPreferences(
        stateData: SubjectNotificationStateData,
        onSuccess: () -> Unit,
        onFailure: (error: Throwable) -> Unit
    ) {
        if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
            notificationsEventHandler.onNotificationsLoading(true)
        }

        try {
            val indetifers = stateData.notificationTypeIdentifiers.map {
                it.name.toLowerCase(Locale.ROOT)
            }.toSet()

            notificationsDataSource.updateSubjectSubscriptions(
                deviceId = provideDeviceId(),
                subjectId = action.subjectId,
                subjectType = action.subjectType,
                subscribedNotificationTypeIds = indetifers
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
            if (e is IOException || e is HttpException) {
                if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                    notificationsEventHandler.onNotificationsLoading(false)
                    notificationsEventHandler.onPreferencesSavedError(e)
                }
                onFailure(e)
            } else throw e
        }
    }
}