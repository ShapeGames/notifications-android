package dk.shape.games.notifications.actions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class EventNotificationsAction(
    @SerializedName("includePlacements") val includePlacements: Boolean,
    @SerializedName("filterEventIds") val filterEventIds: List<String>? = null
) : Parcelable