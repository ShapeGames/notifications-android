package dk.shape.games.notifications.presentation

interface EventNotificationsSheetEventHandler {
    /**
     * Called when user presses toolbar back button on Notification screen
     */
    fun onDismissed()

    /**
     * Called when the number of active subscriptions has changed for an event
     */
    fun onSubscriptionsUpdated(eventId: String, hasActiveSubscriptions: Boolean)
}