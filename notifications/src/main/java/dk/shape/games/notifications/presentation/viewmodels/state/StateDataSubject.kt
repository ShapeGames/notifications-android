package dk.shape.games.notifications.presentation.viewmodels.state

import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.entities.SubjectType

data class StateDataSubject(
    val subjectId: String,
    val subjectType: SubjectType,
    val notificationTypeIds: List<SubjectNotificationIdentifier>
)