package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.R
import dk.shape.games.uikit.databinding.UIText

data class NotificationsSettingsHeaderViewModel(
    val title: UIText,
    private val isFirst: Boolean = false
) {
    val marginTopRes: Int = if (isFirst) {
        R.dimen.empty_padding
    } else R.dimen.notifications_vertical_padding
}