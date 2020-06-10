package dk.shape.games.notifications.usecases

import dk.shape.danskespil.module.data.entities.Icon
import dk.shape.games.notifications.aliases.NotificationType
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import java.util.*

internal sealed class EventNotificationTypesState {

    data class Content(
        val eventId: String,
        val eventNameLine1: String?,
        val eventNameLine2: String?,
        val eventStartDate: Date?,
        val eventIcon: Icon?,
        val level2Name: String?,
        val level3Name: String?,
        val notificationTypesIds: Set<String>
        ) : EventNotificationTypesState() {

        companion object {

            fun create(event: Event, notificationTypes: Set<NotificationType>): Content =
                Content(
                    event.id,
                    if (event.hasHomeAndAway()) event.home else event.name,
                    if (event.hasHomeAndAway()) event.away else null,
                    event.scheduledStartTime,
                    event.icon,
                    if (event.levelPath.size >= 2) event.levelPath.getOrNull(1)?.name else null,
                    if (event.levelPath.size >= 3) event.levelPath.getOrNull(event.levelPath.size - 1)?.name else null,
                    notificationTypes.map { it.identifier }.toSet()
                )

        }

    }

    object Loading : EventNotificationTypesState()

    object Error : EventNotificationTypesState()

}