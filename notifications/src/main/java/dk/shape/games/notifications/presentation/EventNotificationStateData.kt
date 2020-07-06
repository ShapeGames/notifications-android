package dk.shape.games.notifications.presentation

import dk.shape.games.notifications.aliases.LegacyNotificationType

data class EventNotificationStateData(
    val eventId: String,
    val notificationTypeIds: List<LegacyNotificationType>
)