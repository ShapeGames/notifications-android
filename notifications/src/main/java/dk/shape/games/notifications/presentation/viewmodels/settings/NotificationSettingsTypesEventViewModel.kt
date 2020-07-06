package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationSheetEventViewModel

internal data class NotificationSettingsTypesEventViewModel(
    val notificationViewModel: NotificationSheetEventViewModel,
    private val onBackPressed: () -> Unit
) {
    val toolbarViewModel: NotificationsToolbarViewModel =
        NotificationsToolbarViewModel.NotificationTypes(
            onBackPressed
        )
}