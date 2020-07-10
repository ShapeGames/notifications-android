package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.aliases.SubjectNotificationType

internal fun SubjectNotificationGroup.toDefaultOrAllNotificationTypes(): Set<SubjectNotificationType> =
    defaultNotificationTypeIdentifiers.toNotificationTypes(notificationTypes).let { defaultTypes ->
        if (defaultTypes.isEmpty()) {
            notificationTypes.toSet()
        } else defaultTypes
    }