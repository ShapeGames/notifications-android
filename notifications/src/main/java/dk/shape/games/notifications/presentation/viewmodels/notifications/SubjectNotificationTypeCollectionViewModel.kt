package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.value
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding

internal data class SubjectNotificationTypeCollectionViewModel(
    private var selectedIdentifiers: Set<SubjectNotificationIdentifier>,
    private val defaultIdentifiers: Set<SubjectNotificationIdentifier>,
    private val activatedIdentifiers: Set<SubjectNotificationIdentifier>,
    private val possibleTypes: Set<SubjectNotificationType>,
    private val selectionNotifier: (hasSelections: Boolean) -> Unit,
    internal val initialMasterState: Boolean
) {
    val itemBinding = ItemBinding.of<NotificationTypeViewModel>(
        BR.viewModel,
        R.layout.view_notifications_type_item
    )

    val notificationTypeItems: ObservableField<List<NotificationTypeViewModel>> =
        ObservableField(possibleTypes.mapIndexed { index, notificationType ->
            notificationType.toNotificationTypeViewModel(
                isLast = index == possibleTypes.size - 1,
                onNotificationTypeSelected = { isSelected ->
                    if (isSelected) {
                        selectedIdentifiers += notificationType.identifier
                    } else {
                        selectedIdentifiers -= notificationType.identifier
                    }
                    selectionNotifier(selectedIdentifiers.isNotEmpty())
                },
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
            forEach { viewModel ->
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
        initialIdentifiers: Set<SubjectNotificationIdentifier>,
        currentIdentifiers: Set<SubjectNotificationIdentifier>
    ): Boolean {
        val sequenceOne = initialIdentifiers.sorted().map { it.name }.joinToString { it }
        val sequenceTwo = currentIdentifiers.sorted().map { it.name }.joinToString { it }
        return sequenceOne == sequenceTwo
    }
}