package dk.shape.games.notifications.entities

import com.google.gson.annotations.SerializedName

/**
 * Entity representing all subject types that can be used for stats notifications.
 */
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