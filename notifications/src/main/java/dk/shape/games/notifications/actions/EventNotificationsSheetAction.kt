package dk.shape.games.notifications.actions

import android.os.Parcelable
import dk.shape.games.notifications.extensions.toEventInfo
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventNotificationsSheetAction(
    val eventId: String,
    val eventInfo: EventInfo,
    val groupId: String
) : Parcelable

fun Event.toEventNotificationSheetAction() = EventNotificationsSheetAction(
    eventId = id,
    groupId = notificationConfigurationId,
    eventInfo = toEventInfo()
)