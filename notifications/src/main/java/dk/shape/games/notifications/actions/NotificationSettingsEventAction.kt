package dk.shape.games.notifications.actions

import android.os.Parcelable
import dk.shape.games.notifications.aliases.LegacyNotificationType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationSettingsEventAction(
    val eventId: String,
    val eventInfo: EventInfo,
    val possibleNotifications: List<LegacyNotificationType>,
    val initialActiveNotificationIds: Set<String>,
    val defaultNotificationIds: Set<String>
) : Parcelable
