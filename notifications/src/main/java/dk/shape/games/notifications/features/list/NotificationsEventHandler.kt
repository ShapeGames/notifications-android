package dk.shape.games.notifications.features.list

interface NotificationsEventHandler {

    /**
     * Called when user presses toolbar back button on Notification screen
     */
    fun onBackPress(notificationsFragment: NotificationsFragment)

    /**
     * Called when user presses a configuration button displayed on a Notification
     */
    fun onConfigurationClick(notificationsFragment: NotificationsFragment, forEventId: String)

    /**
     * Called when an error occurs while toggling a notification. By default, the Notifications
     * screen just switches the switch back to its original position. Here, you can optionally show
     * an error popup or a dialog to notify the user.
     */
    fun onToggleError(e: Throwable)

}