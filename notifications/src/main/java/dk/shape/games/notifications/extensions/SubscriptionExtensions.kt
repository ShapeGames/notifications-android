package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.entities.Subscription
import java.util.*

internal fun Subscription.toActiveNotificationTypes(
    notificationGroup: SubjectNotificationGroup
): Set<SubjectNotificationType> = types.mapNotNull { subscriptionType ->
    notificationGroup.notificationTypes.find { notificationType ->
        notificationType.identifier.name.toLowerCase(Locale.ROOT) == subscriptionType
    }
}.toSet()
