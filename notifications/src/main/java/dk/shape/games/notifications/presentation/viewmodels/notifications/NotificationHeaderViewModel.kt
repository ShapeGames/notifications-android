package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import dk.shape.games.notifications.actions.EventInfo
import dk.shape.games.notifications.extensions.toDateText
import dk.shape.games.notifications.extensions.toTimeText
import dk.shape.games.uikit.databinding.UIImage
import dk.shape.games.uikit.databinding.UIText

internal sealed class NotificationHeaderViewModel(
    open val isDisabled: ObservableBoolean,
    open val isHeaderTitleVisible: Boolean,
    private val onSwitchToggled: (isChecked: Boolean) -> Unit
) {
    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        onSwitchToggled(isChecked)
    }

    val titleVisibility = if (isHeaderTitleVisible) View.VISIBLE else View.GONE

    data class Subject(
        val name: String,
        override val isDisabled: ObservableBoolean,
        override val isHeaderTitleVisible: Boolean,
        private val onSwitchToggled: (isChecked: Boolean) -> Unit
    ) : NotificationHeaderViewModel(
        isDisabled,
        isHeaderTitleVisible,
        onSwitchToggled
    )

    data class Event(
        val sportIcon: UIImage,
        val homeName: String,
        val awayName: String?,
        val dateText: UIText,
        val timeText: UIText,
        val level2Name: String?,
        val level3Name: String?,
        override val isDisabled: ObservableBoolean,
        override val isHeaderTitleVisible: Boolean,
        private val onSwitchToggled: (isChecked: Boolean) -> Unit
    ) : NotificationHeaderViewModel(
        isDisabled,
        isHeaderTitleVisible,
        onSwitchToggled
    )
}

internal fun EventInfo.toNotificationHeaderEventViewModel(
    isDisabled: ObservableBoolean,
    onSwitchToggled: (isChecked: Boolean) -> Unit
): NotificationHeaderViewModel.Event =
    NotificationHeaderViewModel.Event(
        sportIcon = sportIconName?.toUIImage() ?: UIImage.gone(),
        homeName = homeName.capitalize(),
        awayName = awayName?.capitalize(),
        dateText = startDate.toDateText(),
        timeText = startDate.toTimeText(),
        level2Name = level2Name?.capitalize(),
        level3Name = level3Name?.capitalize(),
        isDisabled = isDisabled,
        isHeaderTitleVisible = false,
        onSwitchToggled = onSwitchToggled
    )

private fun String.toUIImage(): UIImage = UIImage.byResourceName(replace('-', '_'))