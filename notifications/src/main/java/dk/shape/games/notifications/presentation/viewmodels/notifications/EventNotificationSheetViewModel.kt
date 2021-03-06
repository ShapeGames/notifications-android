package dk.shape.games.notifications.presentation.viewmodels.notifications

import dk.shape.games.notifications.presentation.viewmodels.settings.NotificationsToolbarViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel

internal data class EventNotificationSheetViewModel(
    val notificationViewModel: NotificationSheetEventViewModel,
    val notificationSwitcherViewModel: EventNotificationSwitcherViewModel,
    val errorMessageViewModel: ErrorMessageViewModel,
    private val onBackPressed: () -> Unit
) {
    val toolbarViewModel: NotificationsToolbarViewModel =
        NotificationsToolbarViewModel.NotificationTypes(
            onBackPressed
        )
}