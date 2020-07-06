package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import androidx.databinding.ObservableBoolean

internal data class NotificationSaveButtonViewModel(
    private val onButtonClicked: (onSaving: () -> Unit, onError: () -> Unit) -> Unit
) {
    val hasStateChanges: ObservableBoolean = ObservableBoolean(false)

    val isSavingPreferences: ObservableBoolean = ObservableBoolean(false)

    val onPreferencesSavedListener = View.OnClickListener {
        onButtonClicked(
            { isSavingPreferences.set(true) },
            { isSavingPreferences.set(false) }
        )
    }
}