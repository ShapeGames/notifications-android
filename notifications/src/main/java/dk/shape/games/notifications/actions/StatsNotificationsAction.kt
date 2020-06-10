package dk.shape.games.notifications.actions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import dk.shape.games.notifications.entities.SubjectType
import kotlinx.android.parcel.Parcelize

@Parcelize
class StatsNotificationsAction(
    @SerializedName("sport_id") val sportId: String,
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("subject_name") val subjectName: String,
    @SerializedName("subject_type") val subjectType: SubjectType,
    @SerializedName("has_notifications") val hasNotifications: Boolean
) : Parcelable