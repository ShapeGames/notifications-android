package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean

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
}