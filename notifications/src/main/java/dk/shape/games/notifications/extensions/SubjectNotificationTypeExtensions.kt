package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.SubjectNotificationType

internal fun Set<SubjectNotificationType>.toTypeIds(): Set<String> = map { notificationType ->
    notificationType.identifier.toTypeId()
}.toSet()

internal fun SubjectNotificationType.isChecked(selectedTypes: Set<SubjectNotificationType>) =
    selectedTypes.any { notificationType ->
        notificationType.identifier == identifier
    }