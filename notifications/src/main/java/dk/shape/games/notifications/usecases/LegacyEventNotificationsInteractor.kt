package dk.shape.games.notifications.usecases

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import dk.shape.componentkit2.Result
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription

data class LegacyEventNotificationsInteractor(
    private val notificationComponent: NotificationsComponentInterface
) : LegacyEventNotificationsUseCases {

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
                    subscription.commaSeparatedTypes.isNotEmpty()
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
        onError: () -> Unit
    ) {
        if (notificationTypeIds.isNotEmpty()) {
            subscribeToNotifications(
                eventId,
                notificationTypeIds,
                onError
            )
        } else unsubscribeAllNotifications(
            eventId,
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
        onError: () -> Unit
    ) {
        notificationComponent.subscribe(
            eventId,
            notificationTypeIds.joinToString(",")
        ).onMainResult { subscriptionResult ->
            if (subscriptionResult.resultType != Result.ResultType.SUCCESS) {
                onError()
            }
        }
    }

    @MainThread
    private fun unsubscribeAllNotifications(
        eventId: String,
        onError: () -> Unit
    ) {
        notificationComponent.unsubscribe(
            eventId
        ).onMainResult { subscriptionResult ->
            if (subscriptionResult.resultType != Result.ResultType.SUCCESS) {
                onError()
            }
        }
    }
}