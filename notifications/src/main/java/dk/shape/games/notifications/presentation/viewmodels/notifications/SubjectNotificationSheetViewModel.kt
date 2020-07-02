package dk.shape.games.notifications.presentation.viewmodels.notifications

import dk.shape.games.notifications.presentation.viewmodels.settings.NotificationsToolbarViewModel

internal data class SubjectNotificationSheetViewModel(
    val notificationViewModel: SubjectNotificationViewModel,
    val notificationSwitcherViewModel: SubjectNotificationSwitcherViewModel,
    private val onBackPressed: () -> Unit
) {
    val toolbarViewModel: NotificationsToolbarViewModel =
        NotificationsToolbarViewModel.NotificationTypes(
            onBackPressed
        )
}