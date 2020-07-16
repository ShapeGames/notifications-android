package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationSheetEventViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel

internal data class NotificationSettingsTypesEventViewModel(
    val notificationViewModel: NotificationSheetEventViewModel,
    val errorMessageViewModel: ErrorMessageViewModel,
    private val onBackPressed: () -> Unit
) {
    val toolbarViewModel: NotificationsToolbarViewModel =
        NotificationsToolbarViewModel.NotificationTypes(
            onBackPressed
        )
}