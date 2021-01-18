package dk.shape.games.notifications.usecases

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import dk.shape.componentkit2.Result
import dk.shape.danskespil.foundation.DSApiResponseException
import dk.shape.games.notifications.aliases.EventNotificationsLoadedListener
import dk.shape.games.notifications.aliases.LegacyNotificationGroup
import dk.shape.games.notifications.aliases.LegacyNotificationType
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                val activatedTypes: Set<String> = subscriptions.find { subscription ->
                    subscription.eventId == eventId
                }?.types?.toSet() ?: emptySet()

                val possibleTypes: List<LegacyNotificationType> = matchingGroup.notificationTypes

                val defaultTypes: Set<String> =
                    matchingGroup.defaultNotificationTypeIdentifiers.toSet()

                postToMain { onSuccess(activatedTypes, possibleTypes, defaultTypes) }
            } ?: run {
                postToMain { onError() }
            }
        } catch (e: Exception) {
            postToMain { onError() }
        }
    }

    override suspend fun loadAllSubscriptions(
        providedEventIds: List<String>?,
        includeAllEvents: Boolean,
        appConfig: AppConfig,
        onSaveEventIds: (List<String>) -> Unit,
        provideEvents: suspend (eventIds: List<String>) -> List<Event>
    ): List<LoadedLegacySubscription> {
        val subscriptions = getAllSubscriptions()
        val subscriptionsToShow: List<Subscription> =
            if (includeAllEvents)
                subscriptions.plus(subscriptions.getUnsubscribedEvents(providedEventIds))
            else subscriptions

        val eventIdsToShow =
            providedEventIds ?: subscriptionsToShow.map { it.eventId }
                .takeUnless { includeAllEvents }
            ?: emptyList()

        onSaveEventIds(eventIdsToShow)

        return try {
            provideEvents(eventIdsToShow)
                .mapNotNull { event ->

                    appConfig.notifications.group.find { notificationGroup ->
                        notificationGroup.groupId == event.notificationConfigurationId
                    }?.let { matchingGroup ->

                        subscriptionsToShow.find { subscription ->
                            subscription.eventId == event.id
                        }?.takeIf { subscription ->
                            includeAllEvents || subscription.types.isNotEmpty()
                        }?.let { matchingSubscription ->
                            LoadedLegacySubscription(
                                event = event,
                                subscription = matchingSubscription,
                                notificationGroup = matchingGroup
                            )
                        }
                    }
                }
        } catch (e: DSApiResponseException.MissingBodyError) {
            emptyList()
        }
    }

    private fun List<Subscription>.getUnsubscribedEvents(
        providedEventIds: List<String>?
    ): List<Subscription> {
        val subscribedEventIds: List<String> =
            map { subscription ->
                subscription.eventId
            }

        return providedEventIds?.filterNot { providedId ->
            subscribedEventIds.contains(providedId)
        }?.map { id ->
            Subscription(
                eventId = id,
                types = emptyList()
            )
        } ?: emptyList()
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

    private suspend fun postToMain(updateUI: () -> Unit) = withContext(Dispatchers.Main) {
        updateUI()
    }
}