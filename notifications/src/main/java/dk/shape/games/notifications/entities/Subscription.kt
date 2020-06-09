package dk.shape.games.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing all the notification types that user is subscribed to for a certain event.
 */
@Serializable
data class Subscription(
    @SerialName("event_id") val eventId: String? = null,
    @SerialName("subject_id") val subjectId: String? = null,
    @SerialName("subject_type") val subjectType: SubjectType? = null,
    @SerialName("types") val types: Set<String> = setOf()
)