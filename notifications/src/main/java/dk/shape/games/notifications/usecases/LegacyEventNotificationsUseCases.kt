package dk.shape.games.notifications.usecases

import dk.shape.games.sportsbook.offerings.modules.notification.Subscription

interface LegacyEventNotificationsUseCases {

    fun getAllSubscriptions(): List<Subscription>

    fun updateNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    )
}