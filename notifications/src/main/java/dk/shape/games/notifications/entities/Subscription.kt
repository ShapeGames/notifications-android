package dk.shape.games.notifications.entities

import kotlinx.serialization.*

/**
 * Entity representing all the notification types that user is subscribed to for a certain event.
 */
@Serializable
sealed class Subscription {
    abstract val types: Set<String>

    @Serializable
    data class Events(
        @SerialName("event_id")
        val eventId: String,

        @SerialName("types")
        override val types: Set<String> = setOf()
    ) : Subscription()

    @Serializable
    data class Stats(
        @SerialName("subject_id")
        val subjectId: String,

        @SerialName("subject_type")
        val subjectType: SubjectType,

        @SerialName("types")
        override val types: Set<String> = setOf()
    ) : Subscription()
}
