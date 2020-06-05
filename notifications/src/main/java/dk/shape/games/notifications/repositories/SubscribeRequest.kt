package dk.shape.games.notifications.repositories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeRequest(
    @SerialName("device_uuid") val deviceId: String,
    @SerialName("event_id") val eventId: String,
    @SerialName("types") val types: List<String>
)