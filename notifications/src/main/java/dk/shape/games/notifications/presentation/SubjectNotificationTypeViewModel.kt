package dk.shape.games.notifications.presentation

import android.widget.CompoundButton
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.uikit.databinding.UIImage

internal data class SubjectNotificationTypeViewModel(
    val icon: UIImage,
    val identifier: StatsNotificationIdentifier,
    val notificationName: String,
    private val initialState: Boolean
) {
    var isActivated: Boolean = initialState

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated = isChecked
    }
}