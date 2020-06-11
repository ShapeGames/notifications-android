package dk.shape.games.notifications.aliases

import dk.shape.games.notifications.presentation.SubjectNotificationStateData
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationTypeViewModel

internal typealias PreferencesSaveAction = (stateData: SubjectNotificationStateData, onSuccess: () -> Unit, onFailure: () -> Unit) -> Unit

internal typealias SelectionStateNotifier = (isSelected: Boolean, identifier: StatsNotificationIdentifier) -> Unit

internal typealias NotifificationsLoadedListener = (
    activatedTypes: Set<StatsNotificationType>,
    possibleTypes: List<StatsNotificationType>,
    defaultTypes: Set<StatsNotificationIdentifier>
) -> Unit