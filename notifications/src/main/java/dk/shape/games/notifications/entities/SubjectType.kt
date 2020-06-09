package dk.shape.games.notifications.entities

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Entity representing all subject types that can be used for stats notifications.
 */
@Serializable
enum class SubjectType {
    @SerializedName("events")
    EVENTS,

    @SerializedName("teams")
    TEAMS,

    @SerializedName("leagues")
    LEAGUES,

    @SerializedName("athletes")
    ATHLETES
}