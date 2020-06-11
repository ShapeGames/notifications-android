package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.entities.SubjectType

internal data class SubjectNotificationViewModel(
    val subjectId: String,
    val subjectName: String,
    val subjectType: SubjectType,
    private val onClosedPressed: () -> Unit,
    private val onPreferencesSaved: PreferencesSaveAction
) {
    val savingPreferences: ObservableBoolean = ObservableBoolean(false)

    val hasStateChanges: ObservableBoolean = ObservableBoolean(false)

    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val notificationTypesCollection: ObservableField<SubjectNotificationTypeCollectionViewModel> = ObservableField()

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        activeNotificationState.set(isChecked)
        notificationTypesCollection.get()?.onMasterActive(isChecked)
    }

    val notifySelection = {
        hasStateChanges.set(notificationTypesCollection.get()?.hasChanges == true)
    }

    val onPreferencesSavedListener = View.OnClickListener {
        val hasChanges = notificationTypesCollection.get()?.hasChanges == true

        if (!hasChanges) {
            return@OnClickListener
        }
        val stateData = notificationTypesCollection.get()?.subjectStateData

        savingPreferences.set(stateData != null)

        stateData?.let {
            onPreferencesSaved(it, onClosedPressed) {
                savingPreferences.set(false)
            }
        }
    }
}