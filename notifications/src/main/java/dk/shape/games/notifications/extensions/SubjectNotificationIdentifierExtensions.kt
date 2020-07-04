package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.aliases.SubjectNotificationType
import java.util.*

internal fun List<SubjectNotificationIdentifier>.toNotificationTypes(
    notificationGroupTypes: List<SubjectNotificationType>
): Set<SubjectNotificationType> = mapNotNull { notificationId ->
    notificationGroupTypes.find { notificationType ->
        notificationType.identifier == notificationId
    }
}.toSet()

internal fun Set<SubjectNotificationIdentifier>.toTypeIds(): Set<String> = map { notificationId ->
    notificationId.toTypeId()
}.toSet()

internal fun SubjectNotificationIdentifier.toTypeId(): String = name.toLowerCase(Locale.ROOT)