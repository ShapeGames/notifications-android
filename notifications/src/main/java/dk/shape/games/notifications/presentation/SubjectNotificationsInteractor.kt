package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.SubjectNotificationsLoadedListener
import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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

    private val subscriptionsFilter: (subscription: Subscription) -> Boolean = {
        it.subjectId == action.subjectId && it.subjectType == action.subjectType
    }

    private val sportsIdFilter: (subscription: SubjectNotificationGroup) -> Boolean = {
        it.sportId == action.sportId
    }

    override suspend fun loadNotifications(
        onLoaded: SubjectNotificationsLoadedListener,
        onFailure: (error: Throwable) -> Unit
    ) {
        if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
            notificationsEventHandler.onNotificationsLoading(true)
        }

        try {
            notificationsDataSource.getAllSubscriptions(provideDeviceId()).first()
                .let { subscriptions ->
                    val notifications = notificationsProvider()
                    val notificationsGroup = notifications.find(sportsIdFilter)

                    if (notificationsGroup != null) {
                        val subscription = subscriptions.find(subscriptionsFilter)

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
        } catch (e: Exception) {
            handleException(e, notificationsEventHandler, onFailure)
        }
    }

    override suspend fun saveNotificationPreferences(
        stateData: StateDataSubject,
        onSuccess: () -> Unit,
        onFailure: (error: Throwable) -> Unit
    ) {
        if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
            notificationsEventHandler.onNotificationsLoading(true)
        }

        try {
            val notificationTypeIds = stateData.notificationTypeIds.map {
                it.name.toLowerCase(Locale.ROOT)
            }.toSet()

            notificationsDataSource.updateSubjectSubscriptions(
                deviceId = provideDeviceId(),
                subjectId = action.subjectId,
                subjectType = action.subjectType,
                subscribedNotificationTypeIds = notificationTypeIds
            )

            withContext(Dispatchers.Main) {
                if (stateData.notificationTypeIds.isNotEmpty()) {
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
            handleException(e, notificationsEventHandler, onFailure)
        }
    }

    private fun handleException(
        e: Exception,
        notificationsEventHandler: SubjectNotificationsEventHandler,
        onFailure: (error: Throwable) -> Unit
    ) {
        if (e is IOException || e is HttpException) {
            if (notificationsEventHandler is SubjectNotificationsEventHandler.Full.State) {
                notificationsEventHandler.onNotificationsLoading(false)
                notificationsEventHandler.onPreferencesSavedError(e)
            }
            onFailure(e)
        } else throw e
    }
}