package dk.shape.games.notifications.usecases

import androidx.annotation.WorkerThread
import dk.shape.componentkit2.Result
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription

data class LegacyEventNotificationsInteractor(
    private val notificationComponent: NotificationsComponentInterface
) : LegacyEventNotificationsUseCases {

    @WorkerThread
    override fun getAllSubscriptions(): List<Subscription> {
        return notificationComponent.subscriptions.waitForResult().let { result ->
            when (result.resultType) {
                Result.ResultType.SUCCESS -> result.value
                else -> throw result.error
            }
        }
    }

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