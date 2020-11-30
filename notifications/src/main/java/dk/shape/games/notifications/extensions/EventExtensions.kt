package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.actions.EventInfo
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.event.data.Level

internal fun Event.toTeamNamesPair(): Pair<String, String?> = home?.let { homeTeam ->
    away?.let { awayTeam ->
        Pair(homeTeam, awayTeam)
    }
} ?: run {
    val eventName = name
    val eventNameSplit = eventName.split(" - ").toTypedArray()
    if (eventNameSplit.size != 2) {
        Pair(eventName, null)
    } else Pair(eventNameSplit[0], eventNameSplit[1])
}

internal fun Event.toEventInfo(
    defaultLevel: Int = 2
): EventInfo {
    val teamNames = toTeamNamesPair()

    return EventInfo(
        sportIconName = icon?.name,
        homeName = teamNames.first,
        awayName = teamNames.second,
        startDate = scheduledStartTime,
        // Apply same logic as in offerings
        levelName = levelPath.toLevelPathString(defaultLevel)
    )
}

internal fun List<Level>.toLevelPathString(
    level: Int
): String? = getOrNull(level)?.name