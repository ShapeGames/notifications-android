package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.LegacyNotificationType

internal fun LegacyNotificationType.isChecked(selectedTypeIds: Set<String>) =
    selectedTypeIds.any { typeId ->
        this.identifier == typeId
    }

internal fun List<LegacyNotificationType>.toIds(): Set<String> =
    map {
        it.identifier
    }.toSet()