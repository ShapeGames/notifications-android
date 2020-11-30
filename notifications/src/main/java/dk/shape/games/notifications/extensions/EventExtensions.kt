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

internal fun Event.toEventInfo(): EventInfo {
    val teamNames = toTeamNamesPair()
    return EventInfo(
        sportIconName = icon?.name,
        homeName = teamNames.first,
        awayName = teamNames.second,
        startDate = scheduledStartTime,
        levelName = levelPath.toLastLevelPath()
    )
}

internal fun List<Level>.toLastLevelPath(): String? = if (size > 0) {
    toLevelPath(size - 1)
} else null

internal fun List<Level>.toLevelPath(
    level: Int
): String = get(level).name