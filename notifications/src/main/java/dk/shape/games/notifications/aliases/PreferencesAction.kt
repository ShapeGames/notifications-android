package dk.shape.games.notifications.aliases

import dk.shape.games.notifications.presentation.SubjectNotificationStateData

internal typealias PreferencesSaveAction = (stateSubject: SubjectNotificationStateData, onFailed: () -> Unit, onSuccess: () -> Unit) -> Unit
