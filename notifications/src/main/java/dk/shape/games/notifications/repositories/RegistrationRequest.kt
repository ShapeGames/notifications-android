package dk.shape.games.notifications.repositories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    @SerialName("device_uuid") val deviceId: String,
    @SerialName("platform") val platform: String,
    @SerialName("environment") val environment: String,
    @SerialName("notification_token") val notificationToken: String
)