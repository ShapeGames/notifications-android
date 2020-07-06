package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.R
import dk.shape.games.notifications.BR
import dk.shape.games.uikit.databinding.UIText
import me.tatarka.bindingcollectionadapter2.ItemBinding

private val EMPTY_UI_TEXT = UIText.Raw.Resource(R.string.notifications_none_selected)

sealed class NotificationInfoViewModel {
    data class Icons(
        val items: List<NotificationTypeIconViewModel>
    ) : NotificationInfoViewModel() {
        val itemBinding: ItemBinding<NotificationTypeIconViewModel> =
            ItemBinding.of(BR.viewModel, R.layout.view_notifications_type_icon)
    }

    open class Text(
        val text: UIText
    ) : NotificationInfoViewModel() {
        object Empty : Text(EMPTY_UI_TEXT)
    }
}

internal fun String.toNotificationInfoViewModel(): NotificationInfoViewModel.Text = if (isEmpty()) {
    NotificationInfoViewModel.Text.Empty
} else NotificationInfoViewModel.Text(UIText.Raw.String(this))

internal fun List<NotificationTypeIconViewModel>.toNotificationInfoViewModel(): NotificationInfoViewModel.Icons =
    NotificationInfoViewModel.Icons(this)

