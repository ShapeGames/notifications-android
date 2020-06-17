package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.entities.SubjectType

data class SubjectNotificationStateData(
    val subjectId: String,
    val subjectType: SubjectType,
    val notificationTypeIdentifiers: List<SubjectNotificationIdentifier>
)