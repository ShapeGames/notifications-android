package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.entities.SubjectType

/**
 * created on Wednesday, 02 Dec, 2020
 */

data class TrackingNotificationSavedData(
    val teamName: String,
    val subjectType: SubjectType,
    val selections: List<String>
)