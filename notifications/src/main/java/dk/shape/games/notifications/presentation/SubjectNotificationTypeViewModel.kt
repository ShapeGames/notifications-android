package dk.shape.games.notifications.presentation

import android.widget.CompoundButton
import dk.shape.danskespil.foundation.entities.PolyIcon

internal data class SubjectNotificationTypeViewModel(
    val icon: PolyIcon.Resource,
    val identifier: String,
    val notificationName: String,
    private val initialState: Boolean
) {
    var isActivated: Boolean = initialState

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated = isChecked
    }
}