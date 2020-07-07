package dk.shape.games.notifications.presentation.viewmodels.notifications

import dk.shape.games.notifications.aliases.LegacyNotificationType
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage
import java.util.*

internal data class NotificationTypeInfo(
    val name: String,
    val icon: UIImage? = null,
    val typeId: String
)

internal fun SubjectNotificationType.toNotificationTypeInfo() = NotificationTypeInfo(
    icon = icon.toLocalUIImage(),
    name = name,
    typeId = identifier.name.toLowerCase(Locale.ROOT)
)

internal fun LegacyNotificationType.toNotificationTypeInfo() = NotificationTypeInfo(
    name = name,
    typeId = identifier
)

internal fun Set<SubjectNotificationType>.toNotificationTypeInfos(): List<NotificationTypeInfo> =
    map { it.toNotificationTypeInfo() }

internal fun List<LegacyNotificationType>.toNotificationTypeInfos(): List<NotificationTypeInfo> =
    map { it.toNotificationTypeInfo() }