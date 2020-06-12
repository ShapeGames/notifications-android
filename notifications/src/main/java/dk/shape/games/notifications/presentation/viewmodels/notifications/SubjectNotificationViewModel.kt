package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.extensions.awareSet
import dk.shape.games.notifications.extensions.requireValue
import dk.shape.games.notifications.extensions.value
import dk.shape.games.notifications.presentation.SubjectNotificationStateData

internal data class SubjectNotificationViewModel(
    val subjectId: String,
    val subjectName: String,
    val subjectType: SubjectType,
    private val onClosedPressed: () -> Unit,
    private val onPreferencesSaved: PreferencesSaveAction
) {
    val hasStateChanges: ObservableBoolean = ObservableBoolean(false)
    val isSavingPreferences: ObservableBoolean = ObservableBoolean(false)
    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val notificationTypesCollection: ObservableField<SubjectNotificationTypeCollectionViewModel> =
        ObservableField()

    val stateChangeHandler: (isChecked: Boolean, isMasterTrigger: Boolean) -> Unit =
        { isChecked, isMasterTrigger ->
            if (!isMasterTrigger) {
                activeNotificationState.awareSet(isChecked)
            }
            updateSaveState(isChecked)
        }

    val notifySelection: (hasSelections: Boolean) -> Unit = { hasSelections ->
        if (hasSelections) {
            activeNotificationState.awareSet(hasSelections)
        }
        stateChangeHandler(hasSelections, false)
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

    val onPreferencesSavedListener = View.OnClickListener {
        val hasChanges = notificationTypesCollection.requireValue {
            hasChanges || (initialMasterState != activeNotificationState.get())
        }

        if (hasChanges) {
            val stateData = SubjectNotificationStateData(
                subjectId = subjectId,
                subjectType = subjectType,
                notificationTypeIdentifiers = notificationTypesCollection.value {
                    notificationTypeItems.value { filter { it.isActivated.get() }.map { it.identifier } }
                }.orEmpty()
            )

            isSavingPreferences.set(true)
            notificationTypesCollection.value { allowItemInput(false) }

            onPreferencesSaved(stateData, onClosedPressed) {
                isSavingPreferences.set(false)
                notificationTypesCollection.value { allowItemInput(true) }
            }
        }
    }

    private fun updateSaveState(isChecked: Boolean) {
        val hasChanges = notificationTypesCollection.requireValue {
            hasChanges || (initialMasterState != isChecked)
        }

        hasStateChanges.awareSet(hasChanges)
    }
}