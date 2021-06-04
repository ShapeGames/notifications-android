package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.aliases.LegacyNotificationType
import dk.shape.games.sportsbook.offerings.modules.event.data.Event

internal sealed class EventNotificationState {

    data class Content(
        val eventId: String,
        val eventNameLine1: String?,
        val eventNameLine2: String?,
        val enabledNotificationTypeNames: List<String>
    ) : EventNotificationState() {

        companion object {

            fun create(event: Event, notificationTypes: Set<LegacyNotificationType>): Content {
                return Content(
                    event.id,
                    if (event.hasHomeAndAway()) event.home else event.name,
                    if (event.hasHomeAndAway()) event.away ?: "" else "",
                    notificationTypes.map { it.name }
                )
            }
        }

        fun isEnabled(): Boolean = enabledNotificationTypeNames.isNotEmpty()

    }

    class Error(
        val lastKnownState: Content
    ) : EventNotificationState()

}