package dk.shape.games.notifications.aliases

import dk.shape.games.notifications.presentation.viewmodels.state.StateDataEvent
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject

internal typealias PreferenceSaveSubject = (stateData: StateDataSubject, onSuccess: () -> Unit, onFailure: () -> Unit) -> Unit
internal typealias PreferenceSaveEvent = (stateData: StateDataEvent, onSuccess: () -> Unit, onFailure: () -> Unit) -> Unit

internal typealias OnNotificationTypeSelected = (isSelected: Boolean) -> Unit

internal typealias NotificationsLoadedListener = (
    activatedTypes: Set<SubjectNotificationType>,
    possibleTypes: Set<SubjectNotificationType>,
    defaultTypes: Set<SubjectNotificationIdentifier>
) -> Unit