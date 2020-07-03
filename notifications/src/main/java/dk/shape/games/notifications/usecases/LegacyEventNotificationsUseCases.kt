package dk.shape.games.notifications.usecases

import androidx.annotation.WorkerThread
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription

interface LegacyEventNotificationsUseCases {

    @WorkerThread
    fun getAllSubscriptions(): List<Subscription>

    fun updateNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    )
}