package dk.shape.games.notifications.usecases

import dk.shape.games.sportsbook.offerings.modules.event.data.Event

internal sealed class NotificationsState {

    data class Content(val events: Set<Event>): NotificationsState()

    object Loading : NotificationsState()

    object Error : NotificationsState()

    object Empty : NotificationsState()

}