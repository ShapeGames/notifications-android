package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationSheetSubjectViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel

internal data class NotificationSettingsTypesSubjectViewModel(
    val notificationViewModel: NotificationSheetSubjectViewModel,
    val errorMessageViewModel: ErrorMessageViewModel,
    private val onBackPressed: () -> Unit
) {
    val toolbarViewModel: NotificationsToolbarViewModel =
        NotificationsToolbarViewModel.NotificationTypes(
            onBackPressed
        )
}