package dk.shape.games.notifications.usecases

internal sealed class NotificationTypeState {

    data class Content(
        val notificationTypeId: String,
        val name: String?,
        val enabled: Boolean
    ) : NotificationTypeState()

}