package dk.shape.games.notifications.actions

import android.os.Parcelable
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.entities.SubjectType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationSettingsSubjectAction(
    val subjectName: String,
    val subjectId: String,
    val subjectType: SubjectType,
    val possibleNotifications: Set<SubjectNotificationType>,
    val initialActiveNotifications: Set<SubjectNotificationType>
) : Parcelable