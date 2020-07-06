package dk.shape.games.notifications.presentation.viewmodels.state

data class StateDataEvent(
    val eventId: String,
    val notificationTypeIds: Set<String>
)