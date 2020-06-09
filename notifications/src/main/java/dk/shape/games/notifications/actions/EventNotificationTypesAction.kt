package dk.shape.games.notifications.actions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class EventNotificationTypesAction(
    @SerializedName("eventId") val eventId: String
) : Parcelable