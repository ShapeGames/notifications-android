package dk.shape.games.notifications.features.list

import android.os.Parcelable
import dk.shape.games.notifications.entities.SubjectType

interface StatsNotificationsEventHandler {

    /**
     * Called when user presses toolbar back button on Notification screen
     */
    fun <T: Parcelable> onClosed(actionData: T)

    /**
     * Called when user activates notifications for a specific subject
     */
    fun onNotificationsActivated(subjectId: String, subjectType: SubjectType)

    /**
     * Called when an error occurs while saving notifications preferences. By default, the Notifications
     * screen just switches are just toggled back to their original positions. Here, you can optionally show
     * an error popup or a dialog to notify the user.
     */
    fun onPreferencesSavedError(error: Throwable)

}