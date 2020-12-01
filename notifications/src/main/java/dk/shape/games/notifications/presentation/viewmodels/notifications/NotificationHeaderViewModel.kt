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
    private val onSwitchToggled: (isChecked: Boolean) -> Unit
) {
    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        onSwitchToggled(isChecked)
    }

    data class Subject(
        val name: String,
        override val isDisabled: ObservableBoolean,
        val isHeaderTitleVisible: Boolean,
        private val onSwitchToggled: (isChecked: Boolean) -> Unit
    ) : NotificationHeaderViewModel(
        isDisabled,
        onSwitchToggled
    ){
        val titleVisibility = if (isHeaderTitleVisible) View.VISIBLE else View.GONE
    }

    data class Event(
        val sportIcon: UIImage,
        val homeName: String,
        val awayName: String?,
        val dateText: UIText,
        val timeText: UIText,
        val level2Name: String?,
        val level3Name: String?,
        override val isDisabled: ObservableBoolean,
        private val onSwitchToggled: (isChecked: Boolean) -> Unit
    ) : NotificationHeaderViewModel(
        isDisabled,
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
        onSwitchToggled = onSwitchToggled
    )

private fun String.toUIImage(): UIImage = UIImage.byResourceName(replace('-', '_'))