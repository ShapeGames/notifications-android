package dk.shape.games.notifications.features.list

import android.os.Parcelable
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectNotificationsFragment

interface SubjectNotificationsEventHandler {
    /**
     * Called when user presses toolbar back button on Notification screen
     */
    fun <T: Parcelable> onClosed(notificationsFragment: SubjectNotificationsFragment, actionData: T)

    interface Full: SubjectNotificationsEventHandler {

        /**
         * Called when user activates notifications for a specific subject
         */
        fun onNotificationsActivated(subjectId: String, subjectType: SubjectType)

        /**
         * Called when user deactivates notifications for a specific subject
         */
        fun onNotificationsDeactivated(subjectId: String, subjectType: SubjectType)

        /**
         * Called when user attemps to retrieve notifications data. A loading state of true
         * signifies that the notifications are loading and a loading screen should displayed.
         */
        fun onNotificationsLoading(loading: Boolean)

        /**
         * Called when an error occurs while saving notification preferences. By default, the Notifications
         * screen just switches are just toggled back to their original positions. Here, you can optionally show
         * an error popup or a dialog to notify the user.
         */
        fun onPreferencesSavedError(error: Throwable)

        /**
         * Called when an error occurs while retrieving the notification preferences.
         */
        fun onPreferencesLoadedError(error: Throwable)
    }
}