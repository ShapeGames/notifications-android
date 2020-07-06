package dk.shape.games.notifications.actions

import android.os.Parcelable
import dk.shape.games.notifications.aliases.SubjectNotificationType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubjectNotificationTypesAction(
    val name: String,
    val subjectId: String,
    val possibleNotifications: List<SubjectNotificationType>,
    val initialActiveNotifications: Set<SubjectNotificationType>
) : Parcelable