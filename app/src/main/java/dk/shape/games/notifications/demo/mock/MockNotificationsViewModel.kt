package dk.shape.games.notifications.demo.mock

import android.view.View
import androidx.databinding.ObservableBoolean

internal data class MockNotificationsViewModel(
    private val showNotifications: () -> Unit
) {
    val isLoadingStatus: ObservableBoolean = ObservableBoolean(false)
    val hasNotifications: ObservableBoolean = ObservableBoolean(false)

    val onNotificationsClickedListener = View.OnClickListener {
        showNotifications()
    }
}