package dk.shape.games.notifications.entities

import kotlinx.serialization.*

/**
 * Entity representing all the notification types that user is subscribed to for a certain event.
 */
@Serializable
data class Subscription(
    @SerialName("event_id") val eventId: String? = null,
    @SerialName("subject_id") val subjectId: String = "undefined",
    @SerialName("subject_type") val subjectType: SubjectType = SubjectType.UNKNOWN,
    @SerialName("types") val types: Set<String>
)