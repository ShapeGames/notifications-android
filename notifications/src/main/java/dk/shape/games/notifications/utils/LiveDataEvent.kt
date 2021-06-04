package dk.shape.games.notifications.utils

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
internal abstract class LiveDataEvent {

    protected val handledFlagLock = "lock"

    protected var hasBeenHandled = false
        private set(value) = synchronized(handledFlagLock) {
            field = value
        } // Allow external read but not write

    protected open fun notifyHandled() = synchronized(handledFlagLock) {
        hasBeenHandled = true
    }
}