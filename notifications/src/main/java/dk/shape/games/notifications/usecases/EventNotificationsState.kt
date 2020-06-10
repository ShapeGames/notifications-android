package dk.shape.games.notifications.usecases

import dk.shape.games.sportsbook.offerings.modules.event.data.Event

internal sealed class EventNotificationsState {

    data class Content(val events: Set<Event>): EventNotificationsState()

    object Loading : EventNotificationsState()

    object Error : EventNotificationsState()

    object Empty : EventNotificationsState()

}