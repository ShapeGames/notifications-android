package dk.shape.games.notifications.actions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import dk.shape.games.notifications.entities.SubjectType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventNotificationsSheetAction(
    val eventId: String,
    val eventInfo: EventInfo,
    val groupId: String
) : Parcelable