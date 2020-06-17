package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View

internal data class SubjectNotificationSheetViewModel(
    val notificationViewModel: SubjectNotificationViewModel,
    val notificationSwitcherViewModel: SubjectNotificationSwitcherViewModel,
    private val onClosedPressed: () -> Unit
) {
    val onClosePressedListener = View.OnClickListener {
        onClosedPressed()
    }
}