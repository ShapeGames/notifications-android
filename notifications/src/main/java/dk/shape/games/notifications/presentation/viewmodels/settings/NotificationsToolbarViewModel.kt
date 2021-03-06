package dk.shape.games.notifications.presentation.viewmodels.settings

import android.view.View
import androidx.appcompat.widget.Toolbar
import dk.shape.games.notifications.R
import dk.shape.games.uikit.databinding.UIText

sealed class NotificationsToolbarViewModel(
    val hasTransparentBackground: Boolean = false,
    private val titleRes: Int,
    open val onBackPressed: () -> Unit,
    open val toolbarProvider: (() -> Toolbar)?
) {
    data class Settings(
        override val onBackPressed: () -> Unit,
        override val toolbarProvider: (() -> Toolbar)?
    ) : NotificationsToolbarViewModel(
        titleRes = R.string.notification_settings_title,
        onBackPressed = onBackPressed,
        toolbarProvider = toolbarProvider
    )

    data class NotificationTypes(
        override val onBackPressed: () -> Unit
    ) : NotificationsToolbarViewModel(
        hasTransparentBackground = true,
        titleRes = R.string.notification_types_title,
        onBackPressed = onBackPressed,
        toolbarProvider = null
    )

    val title: UIText = UIText.Raw.Resource(titleRes)

    val backListener = View.OnClickListener {
        onBackPressed()
    }
}