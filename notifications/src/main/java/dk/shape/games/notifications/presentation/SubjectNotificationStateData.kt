package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.entities.SubjectType

internal data class SubjectNotificationStateData (
    val subjectId: String,
    val subjectType: SubjectType,
    val isNotificationActive: Boolean,
    val notificationTypeIdentifiers: List<StatsNotificationIdentifier>
)