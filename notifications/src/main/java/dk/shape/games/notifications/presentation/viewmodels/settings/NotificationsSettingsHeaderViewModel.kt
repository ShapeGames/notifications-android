package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.R
import dk.shape.games.uikit.databinding.UIText

data class NotificationsSettingsHeaderViewModel(
    val title: UIText,
    private val isFirst: Boolean = false
) {
    val marginTopRes: Int = if (isFirst) {
        R.dimen.notifications_header_margin_top_first
    } else R.dimen.notifications_header_margin_top
}