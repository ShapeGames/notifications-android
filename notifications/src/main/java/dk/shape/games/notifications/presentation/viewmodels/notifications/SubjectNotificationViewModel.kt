package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.requireValue
import dk.shape.games.notifications.bindings.value
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectNotificationStateData
import dk.shape.games.notifications.utils.enumValueOrNull

internal data class SubjectNotificationViewModel(
    private val subjectId: String,
    val subjectName: String,
    private val subjectType: SubjectType,
    private val onClosedPressed: () -> Unit,
    private val onPreferencesSaved: PreferencesSaveAction
) {
    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val saveButtonViewModel: NotificationSaveButtonViewModel = NotificationSaveButtonViewModel { onSaving, onError ->
        val hasChanges = notificationTypesCollection.requireValue {
            hasChanges || (initialMasterState != activeNotificationState.get())
        }

        if (hasChanges) {
            val stateData = SubjectNotificationStateData(
                subjectId = subjectId,
                subjectType = subjectType,
                notificationTypeIds = notificationTypesCollection.value {
                    notificationTypeItems.value {
                        filter { viewModel ->
                            viewModel.isActivated.get()
                        }.mapNotNull { viewModel ->
                            enumValueOrNull<SubjectNotificationIdentifier>(viewModel.typeId)
                        }
                    }
                }.orEmpty()
            )

            onSaving()
            notificationTypesCollection.value { allowItemInput(allowInput = false) }

            onPreferencesSaved(stateData, onClosedPressed) {
                onError()
                notificationTypesCollection.value { allowItemInput(allowInput = true) }
            }
        }
    }

    val notificationTypesCollection: ObservableField<NotificationTypeCollectionViewModel> =
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

    private fun updateSaveState(isChecked: Boolean) {
        val hasChanges = notificationTypesCollection.requireValue {
            hasChanges || (initialMasterState != isChecked)
        }

        saveButtonViewModel.hasStateChanges.awareSet(hasChanges)
    }
}