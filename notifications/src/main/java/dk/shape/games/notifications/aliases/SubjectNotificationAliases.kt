package dk.shape.games.notifications.aliases

import dk.shape.games.notifications.presentation.SubjectNotificationStateData

internal typealias PreferencesSaveAction = (stateData: SubjectNotificationStateData, onSuccess: () -> Unit, onFailure: () -> Unit) -> Unit

internal typealias NotificationTypeSelected = (isSelected: Boolean) -> Unit

internal typealias NotificationsLoadedListener = (
    activatedTypes: Set<SubjectNotificationType>,
    possibleTypes: Set<SubjectNotificationType>,
    defaultTypes: Set<SubjectNotificationIdentifier>
) -> Unit