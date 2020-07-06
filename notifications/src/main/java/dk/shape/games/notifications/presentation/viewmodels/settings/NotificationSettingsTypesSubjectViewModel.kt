package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationSheetSubjectViewModel

internal data class NotificationSettingsTypesSubjectViewModel(
    val notificationViewModel: NotificationSheetSubjectViewModel,
    private val onBackPressed: () -> Unit
) {
    val toolbarViewModel: NotificationsToolbarViewModel =
        NotificationsToolbarViewModel.NotificationTypes(
            onBackPressed
        )
}