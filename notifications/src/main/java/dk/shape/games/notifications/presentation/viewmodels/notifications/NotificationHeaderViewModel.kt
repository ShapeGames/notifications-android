package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import dk.shape.games.notifications.actions.NotificationSettingsEventAction
import java.util.*

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
        private val onSwitchToggled: (isChecked: Boolean) -> Unit
    ) : NotificationHeaderViewModel(
        isDisabled,
        onSwitchToggled
    )

    data class Event(
        val sportIconName: String?,
        val homeName: String,
        val awayName: String?,
        val startDate: Date,
        val level2Name: String?,
        val level3Name: String?,
        override val isDisabled: ObservableBoolean,
        private val onSwitchToggled: (isChecked: Boolean) -> Unit
    ) : NotificationHeaderViewModel(
        isDisabled,
        onSwitchToggled
    )
}

internal fun NotificationSettingsEventAction.EventInfo.toNotificationHeaderEventViewModel(
    isDisabled: ObservableBoolean,
    onSwitchToggled: (isChecked: Boolean) -> Unit
): NotificationHeaderViewModel.Event =
    NotificationHeaderViewModel.Event(
        sportIconName = sportIconName,
        homeName = homeName,
        awayName = awayName,
        startDate = startDate,
        level2Name = level2Name,
        level3Name = level3Name,
        isDisabled = isDisabled,
        onSwitchToggled = onSwitchToggled
    )