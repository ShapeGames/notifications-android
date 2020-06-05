package dk.shape.games.notifications.utils

/**
 * An event exposed via a LiveData that conveys some content data about the event that happened.
 */
internal class ContentLiveDataEvent<out T>(private val content: T) : LiveDataEvent() {

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? = synchronized(handledFlagLock) {
        return if (hasBeenHandled) {
            null
        } else {
            notifyHandled()
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}