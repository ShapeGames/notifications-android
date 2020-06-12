package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.extensions.awareSet
import dk.shape.games.notifications.extensions.value

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

    val notificationTypesCollection: ObservableField<SubjectNotificationTypeCollectionViewModel> =
        ObservableField()

    val stateChangeHandler: (isChecked: Boolean, isMasterTrigger: Boolean) -> Unit = { isChecked, isMasterTrigger ->
        if (!isMasterTrigger) {
            activeNotificationState.awareSet(isChecked)
        }
        updateSaveState(isChecked)
    }
    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        activeNotificationState.awareSet(isChecked) {
            notificationTypesCollection.value {
                resetAll()
                onMasterActive(isChecked)
            }
        }
        stateChangeHandler(isChecked, true)
    }

    val notifySelection: (hasSelections: Boolean) -> Unit = { hasSelections ->
        if (hasSelections) {
            activeNotificationState.awareSet(hasSelections)
        }
        stateChangeHandler(hasSelections, false)
    }

    val onPreferencesSavedListener = View.OnClickListener {
        val initialState = notificationTypesCollection.value { initialMasterState }
        val hasChanges = notificationTypesCollection.value { hasChanges } == true

        if (hasChanges || (initialState != activeNotificationState.get())) {
            val stateData = notificationTypesCollection.value { subjectStateData }

            savingPreferences.set(stateData != null)

            stateData?.let {
                onPreferencesSaved(it, onClosedPressed) {
                    savingPreferences.set(false)
                }
            }
        }
    }

    private fun updateSaveState(isChecked: Boolean) {
        val initialState = notificationTypesCollection.value { initialMasterState }
        val hasChanges = notificationTypesCollection.value { hasChanges } == true

        hasStateChanges.awareSet(hasChanges || (initialState != isChecked))
    }
}