package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.SelectionStateNotifier
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.extensions.awareSet
import dk.shape.games.notifications.extensions.value
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding

internal data class SubjectNotificationTypeCollectionViewModel(
    private var selectedIdentifiers: Set<StatsNotificationIdentifier>,
    private val defaultIdentifiers: Set<StatsNotificationIdentifier>,
    private val activatedIdentifiers: Set<StatsNotificationIdentifier>,
    private val possibleTypes: List<StatsNotificationType>,
    private val selectionNotifier: (hasSelections: Boolean) -> Unit,
    internal val initialMasterState: Boolean
) {
    private val stateNotifier: SelectionStateNotifier = { isSelected, identifier ->
        if (isSelected) {
            selectedIdentifiers += identifier
        } else {
            selectedIdentifiers -= identifier
        }
        selectionNotifier(selectedIdentifiers.isNotEmpty())
    }

    val itemBinding = ItemBinding.of<SubjectNotificationTypeViewModel>(
        BR.viewModel,
        R.layout.view_subject_notifications_type_item
    )

    val notificationTypeItems: ObservableField<List<SubjectNotificationTypeViewModel>> =
        ObservableField(possibleTypes.mapIndexed { index, element ->
            element.toNotificationTypeViewModel(
                isLastElement = index == possibleTypes.size - 1,
                selectionStateNotifier = stateNotifier,
                activatedIdentifiers = activatedIdentifiers,
                defaultNofification = defaultIdentifiers
            )
        })

    val hasChanges: Boolean
        get() = !compareIndetifiers(
            initialIdentifiers = activatedIdentifiers.toSet(),
            currentIdentifiers = selectedIdentifiers
        )

    fun resetAll() {
        selectedIdentifiers = activatedIdentifiers.toSet()
    }

    fun allowItemInput(allowInput: Boolean) {
        notificationTypeItems.value {
            forEach {
                it.isEnabled.set(allowInput)
            }
        }
    }

    fun onMasterActive(isActive: Boolean) {
        notificationTypeItems.value {
            forEach {
                if (isActive) {
                    if (defaultIdentifiers.isNotEmpty()) {
                        it.isActivated.awareSet(defaultIdentifiers.contains(it.identifier)) {
                            selectedIdentifiers += it.identifier
                        }
                    } else {
                        it.isActivated.awareSet(true)
                        selectedIdentifiers += it.identifier
                    }
                } else {
                    it.isActivated.awareSet(false)
                    selectedIdentifiers -= it.identifier
                }
            }
        }
    }

    private fun compareIndetifiers(
        initialIdentifiers: Set<StatsNotificationIdentifier>,
        currentIdentifiers: Set<StatsNotificationIdentifier>
    ): Boolean {
        val sequenceOne = initialIdentifiers.sorted().map { it.name }.joinToString { it }
        val sequenceTwo = currentIdentifiers.sorted().map { it.name }.joinToString { it }
        return sequenceOne == sequenceTwo
    }
}