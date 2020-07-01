package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.aliases.SubjectNotificationType

internal fun SubjectNotificationGroup.toDefaultNotificationTypes(): Set<SubjectNotificationType> =
    defaultNotificationTypeIdentifiers.toNotificationTypes(notificationTypes)