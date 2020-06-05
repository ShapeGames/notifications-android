package dk.shape.games.notifications.features.types

interface NotificationTypesEventHandler {

    fun onBackPress(notificationTypesFragment: NotificationTypesFragment)

    /**
     * Called when an error occurs while toggling a notification type. By default, the Notifications
     * Types screen just switches the switch back to its original position. Here, you can optionally
     * show an error popup or a dialog to notify the user.
     */
    fun onToggleError(e: Throwable)

}