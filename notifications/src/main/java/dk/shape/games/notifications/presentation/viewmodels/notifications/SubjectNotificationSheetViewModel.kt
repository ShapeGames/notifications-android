package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectNotificationStateData

internal data class SubjectNotificationSheetViewModel(
    val screenTitle: String,
    val notificationViewModel: SubjectNotificationViewModel,
    val notificationSwitcherViewModel: SubjectNotificationSwitcherViewModel,
    private val onClosedPressed: () -> Unit
) {

    val onClosePressedListener = View.OnClickListener {
        onClosedPressed()
    }
}