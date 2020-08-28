package dk.shape.games.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing all subject types that can be used for stats notifications.
 */
@Serializable
enum class SubjectType {
    @SerialName("events")
    EVENTS,

    @SerialName("teams")
    TEAMS,
    
    @SerialName("leagues")
    LEAGUES,

    @SerialName("athletes")
    ATHLETES,

    UNKNOWN
}