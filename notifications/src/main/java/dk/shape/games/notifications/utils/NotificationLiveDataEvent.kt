package dk.shape.games.notifications.utils

/**
 * An event exposed via a LiveData that doesn't convey any information just notifies that something
 * happened.
 */
internal class NotificationLiveDataEvent : LiveDataEvent() {

    fun shouldBeHandled(): Boolean = synchronized(handledFlagLock) {
        val handledAlready = hasBeenHandled
        notifyHandled()
        return handledAlready
    }
}