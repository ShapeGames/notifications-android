package dk.shape.games.notifications.actions

import android.os.Parcelable
import dk.shape.games.notifications.aliases.LegacyNotificationType
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class NotificationSettingsEventAction(
    val eventId: String,
    val eventInfo: EventInfo,
    val possibleNotifications: List<LegacyNotificationType>,
    val initialActiveNotificationIds: Set<String>
) : Parcelable {
    @Parcelize
    data class EventInfo(
        val sportIconName: String?,
        val homeName: String,
        val awayName: String?,
        val startDate: Date,
        val level2Name: String?,
        val level3Name: String?
    ) : Parcelable
}

