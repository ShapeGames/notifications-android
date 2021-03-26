package dk.shape.games.notifications.entities

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*

/**
 * Entity representing all the notification types that user is subscribed to for a certain event.
 */
@Serializable
data class LegacySubscription(
    @SerializedName("event_id") @SerialName("event_id") val eventId: String,
    @SerializedName("types") @SerialName("types") var types: Set<String> = emptySet<String>()
)