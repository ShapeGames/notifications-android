package dk.shape.games.notifications.usecases

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import dk.shape.componentkit2.Result
import dk.shape.games.notifications.aliases.EventNotificationsLoadedListener
import dk.shape.games.notifications.aliases.LegacyNotificationGroup
import dk.shape.games.notifications.aliases.LegacyNotificationType
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription
import java.lang.Exception

data class LegacyEventNotificationsInteractor(
    private val notificationComponent: NotificationsComponentInterface
) : LegacyEventNotificationsUseCases {

    override suspend fun loadSubscription(
        eventId: String,
        notificationGroupId: String,
        provideNotifications: suspend () -> List<LegacyNotificationGroup>,
        onSuccess: EventNotificationsLoadedListener,
        onError: () -> Unit
    ) {
        try {
            val subscriptions: List<Subscription> = getAllSubscriptions()
            val notificationGroups: List<LegacyNotificationGroup> = provideNotifications()

            notificationGroups.find { notificationGroup ->
                notificationGroup.groupId == notificationGroupId
            }?.let { matchingGroup ->
                subscriptions.find { subscription ->
                    subscription.eventId == eventId
                }?.takeIf { subscription ->
                    subscription.types.isNotEmpty()
                }?.let { matchingSubscription ->
                    val activatedTypes: Set<String> = matchingSubscription.types.toSet()
                    val possibleTypes: List<LegacyNotificationType> =
                        matchingGroup.notificationTypes
                    val defaultTypes: Set<String> =
                        matchingGroup.defaultNotificationTypeIdentifiers.toSet()

                    onSuccess(activatedTypes, possibleTypes, defaultTypes)
                }
            } ?: onError()
        } catch (e: Exception) {
            onError()
        }
    }

    override suspend fun loadAllSubscriptions(
        eventIds: List<String>?,
        appConfig: AppConfig,
        onSaveEventIds: (List<String>) -> Unit,
        provideEvents: suspend (eventIds: List<String>) -> List<Event>
    ): List<LoadedLegacySubscription> {
        val subscriptions = getAllSubscriptions()
        val subscribedEventIds = eventIds ?: subscriptions.map { it.eventId }
        onSaveEventIds(subscribedEventIds)

        return provideEvents(subscribedEventIds).mapNotNull { event ->

            appConfig.notifications.group.find { notificationGroup ->
                notificationGroup.groupId == event.notificationConfigurationId
            }?.let { matchingGroup ->

                subscriptions.find { subscription ->
                    subscription.eventId == event.id
                }?.takeIf { subscription ->
                    subscription.types.isNotEmpty()
                }?.let { matchingSubscription ->
                    LoadedLegacySubscription(
                        event = event,
                        subscription = matchingSubscription,
                        notificationGroup = matchingGroup
                    )
                }
            }
        }
    }

    @MainThread
    override fun updateNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        if (notificationTypeIds.isNotEmpty()) {
            subscribeToNotifications(
                eventId,
                notificationTypeIds,
                onSuccess,
                onError
            )
        } else unsubscribeAllNotifications(
            eventId,
            onSuccess,
            onError
        )
    }

    @WorkerThread
    private fun getAllSubscriptions(): List<Subscription> {
        return notificationComponent.subscriptions.waitForResult().let { result ->
            when (result.resultType) {
                Result.ResultType.SUCCESS -> result.value
                else -> throw result.error
            }
        }
    }

    @MainThread
    private fun subscribeToNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        notificationComponent.subscribe(
            eventId,
            notificationTypeIds.joinToString(",")
        ).onMainResult { subscriptionResult ->
            if (subscriptionResult.resultType == Result.ResultType.SUCCESS) {
                onSuccess()
            } else onError()
        }
    }

    @MainThread
    private fun unsubscribeAllNotifications(
        eventId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        notificationComponent.unsubscribe(
            eventId
        ).onMainResult { unsubscriptionResult ->
            if (unsubscriptionResult.resultType == Result.ResultType.SUCCESS) {
                onSuccess()
            } else onError()
        }
    }
}