package dk.shape.games.notifications.presentation

internal data class SubjectNotificationStateData (
    val subjectId: String,
    val subjectType: String,
    val isNotificationActive: Boolean,
    val notificationTypeIdentifiers: List<String>
)