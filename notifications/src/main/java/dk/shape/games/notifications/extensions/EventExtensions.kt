package dk.shape.games.notifications.extensions

import dk.shape.danskespil.foundation.data.DataFlow
import dk.shape.danskespil.foundation.data.GamesDataResult
import dk.shape.games.notifications.actions.LegacyEventNotificationTypesAction
import dk.shape.games.sportsbook.offerings.generics.event.data.joinIds
import dk.shape.games.sportsbook.offerings.modules.event.data.Event

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

internal fun Event.toEventInfo(): LegacyEventNotificationTypesAction.EventInfo {
    val teamNames = toTeamNamesPair()

    val level2Name = if (levelPath.size >= 2) {
        levelPath.getOrNull(1)?.name
    } else null

    val level3Name = if (levelPath.size >= 3) {
        levelPath.getOrNull(levelPath.size - 1)?.name
    } else null

    return LegacyEventNotificationTypesAction.EventInfo(
        sportIconName = icon?.name,
        homeName = teamNames.first,
        awayName = teamNames.second,
        startDate = scheduledStartTime,
        level2Name = level2Name,
        level3Name = level3Name
    )
}

internal suspend fun DataFlow<String, List<Event>>.getEvents(eventIds: List<String>): List<Event> =
    if (eventIds.isNotEmpty()) {
        getData(this.joinIds(eventIds)).let { result ->
            when (result) {
                is GamesDataResult.Success -> result.value
                is GamesDataResult.Error -> throw result.error
            }
        }
    } else listOf()