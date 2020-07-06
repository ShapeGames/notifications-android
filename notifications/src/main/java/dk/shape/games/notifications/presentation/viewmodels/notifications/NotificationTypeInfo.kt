package dk.shape.games.notifications.presentation.viewmodels.notifications

import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage
import java.util.*

internal data class NotificationTypeInfo(
    val name: String,
    val icon: UIImage,
    val typeId: String
)

internal fun SubjectNotificationType.toNotificationTypeInfo() = NotificationTypeInfo(
    icon = icon.toLocalUIImage(),
    name = name,
    typeId = identifier.name.toLowerCase(Locale.ROOT)
)

internal fun Set<SubjectNotificationType>.toNotificationTypeInfos(): List<NotificationTypeInfo> =
    map { it.toNotificationTypeInfo() }