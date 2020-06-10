package dk.shape.games.notifications.repositories

import dk.shape.games.notifications.entities.SubjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeRequest(
    @SerialName("device_uuid") val deviceId: String,
    @SerialName("event_id") val eventId: String?,
    @SerialName("subject_id") val subjectId: String?,
    @SerialName("subject_type") val subjetType: SubjectType?,
    @SerialName("types") val types: List<String>
)