package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean

internal data class NotificationHeaderViewModel(
    val subjectName: String,
    val isDisabled: ObservableBoolean,
    private val onMasterSwitchToggled: (isChecked: Boolean) -> Unit
) {
    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        onMasterSwitchToggled(isChecked)
    }
}