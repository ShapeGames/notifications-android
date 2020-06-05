package dk.shape.games.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing all the notification types that user is subscribed to for a certain event.
 */
@Serializable
data class Subscription(
    @SerialName("event_id") val eventId: String,
    @SerialName("types") val types: Set<String>
)