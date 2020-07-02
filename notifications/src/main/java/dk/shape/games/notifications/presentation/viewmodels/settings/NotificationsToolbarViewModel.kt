package dk.shape.games.notifications.presentation.viewmodels.settings

import android.view.View
import dk.shape.games.notifications.R
import dk.shape.games.uikit.databinding.UIText

sealed class NotificationsToolbarViewModel(
    val backgroundRes: Int,
    private val titleRes: Int,
    open val onBackPressed: () -> Unit
) {
    data class Settings(
        override val onBackPressed: () -> Unit
    ) : NotificationsToolbarViewModel(
        backgroundRes = R.drawable.notifications_toolbar_background_blue,
        titleRes = R.string.notification_settings_title,
        onBackPressed = onBackPressed
    )

    data class NotificationTypes(
        override val onBackPressed: () -> Unit
    ) : NotificationsToolbarViewModel(
        backgroundRes = R.drawable.notification_toolbar_background,
        titleRes = R.string.notification_types_title,
        onBackPressed = onBackPressed
    )

    val title: UIText = UIText.Raw.Resource(titleRes)

    val backListener = View.OnClickListener {
        onBackPressed()
    }
}