package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferenceSaveSubject
import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.requireValue
import dk.shape.games.notifications.bindings.value
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject
import dk.shape.games.notifications.utils.enumValueOrNull

internal data class NotificationSheetSubjectViewModel(
    private val subjectId: String,
    private val subjectName: String,
    private val subjectType: SubjectType,
    private val onClosedPressed: () -> Unit,
    private val onPreferencesSaved: PreferenceSaveSubject
) {
    val saveButtonViewModel: NotificationSaveButtonViewModel =
        NotificationSaveButtonViewModel { onSaving, onError ->
            val hasChanges = notificationTypesCollection.requireValue {
                hasChanges || (initialMasterState != headerViewModel.activeNotificationState.get())
            }

            if (hasChanges) {
                val stateData =
                    StateDataSubject(
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

    private val switchToggleHandler: (Boolean) -> Unit = { isChecked ->
        headerViewModel.activeNotificationState.awareSet(isChecked) {
            notificationTypesCollection.value {
                resetAll()
                onMasterActive(isChecked)
            }
            stateChangeHandler(isChecked, true)
        }
    }

    val headerViewModel: NotificationHeaderViewModel.Subject = NotificationHeaderViewModel.Subject(
        name = subjectName,
        isDisabled = saveButtonViewModel.isSavingPreferences,
        onSwitchToggled = switchToggleHandler
    )

    val notificationTypesCollection: ObservableField<NotificationTypeCollectionViewModel> =
        ObservableField()

    val stateChangeHandler: (isChecked: Boolean, isMasterTrigger: Boolean) -> Unit =
        { isChecked, isMasterTrigger ->
            if (!isMasterTrigger) {
                headerViewModel.activeNotificationState.awareSet(isChecked)
            }
            updateSaveState(isChecked)
        }

    val notifySelection: (hasSelections: Boolean) -> Unit = { hasSelections ->
        if (hasSelections) {
            headerViewModel.activeNotificationState.awareSet(hasSelections)
        }
        stateChangeHandler(hasSelections, false)
    }

    private fun updateSaveState(isChecked: Boolean) {
        val hasChanges = notificationTypesCollection.requireValue {
            hasChanges || (initialMasterState != isChecked)
        }

        saveButtonViewModel.hasStateChanges.awareSet(hasChanges)
    }
}