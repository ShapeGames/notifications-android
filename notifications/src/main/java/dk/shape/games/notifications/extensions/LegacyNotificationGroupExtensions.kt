package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.LegacyNotificationGroup

internal fun LegacyNotificationGroup.toDefaultOrAllNotifications(): Set<String> =
    defaultNotificationTypeIdentifiers.toSet().let { defaultTypeIds ->
        if (defaultTypeIds.isEmpty()) {
            notificationTypes.toIds()
        } else defaultTypeIds
    }