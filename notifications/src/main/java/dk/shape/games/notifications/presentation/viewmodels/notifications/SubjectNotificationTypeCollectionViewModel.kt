package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.SelectionStateNotifier
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectNotificationStateData
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal data class SubjectNotificationTypeCollectionViewModel(
    private val subjectId: String,
    private val subjectType: SubjectType,
    private var selectedIdentifiers: Set<StatsNotificationIdentifier>,
    private var defaultIdentifiers: Set<StatsNotificationIdentifier>,
    private var initialIdentifiers: Set<StatsNotificationIdentifier>,
    private val activatedTypes: Set<StatsNotificationType>,
    private val possibleTypes: List<StatsNotificationType>,
    private val selectionNotifier: (hasSelections: Boolean) -> Unit
) {
    private val stateNotifier: SelectionStateNotifier = { isSelected, identifier ->
        if (isSelected) {
            selectedIdentifiers += identifier
            defaultIdentifiers += identifier
        } else {
            selectedIdentifiers -= identifier
            defaultIdentifiers -= identifier
        }
        selectionNotifier(selectedIdentifiers.isNotEmpty())
    }

    val itemBinding = ItemBinding.of<SubjectNotificationTypeViewModel>(
        BR.viewModel,
        R.layout.view_subject_notifications_type_item
    )

    val notificationTypeItems: ObservableField<List<SubjectNotificationTypeViewModel>> = ObservableField(possibleTypes.mapIndexed { index, element ->
        element.toNotificationTypeViewModel(
            isLastElement = index == possibleTypes.size - 1,
            selectionStateNotifier = stateNotifier,
            activatedNotifications = activatedTypes,
            defaultNofification = defaultIdentifiers
        )
    })

    val subjectStateData: SubjectNotificationStateData
        get() = SubjectNotificationStateData(
            subjectId = subjectId,
            subjectType = subjectType,
            notificationTypeIdentifiers = notificationTypeItems.get().orEmpty()
                .filter { it.isActivated.get() }.map { it.identifier }
        )

    val hasChanges: Boolean
        get() = !compareIndetifiers(
            initialIdentifiers = initialIdentifiers,
            currentIdentifiers = selectedIdentifiers
        )

    private fun compareIndetifiers(
        initialIdentifiers: Set<StatsNotificationIdentifier>,
        currentIdentifiers: Set<StatsNotificationIdentifier>
    ): Boolean {
        val sequenceOne = initialIdentifiers.sorted().map { it.name }.joinToString { it }
        val sequenceTwo = currentIdentifiers.sorted().map { it.name }.joinToString { it }
        return sequenceOne == sequenceTwo
    }

    fun onMasterActive(isActive: Boolean) {
        if (isActive) {
            if (defaultIdentifiers.isNotEmpty()) {
                notificationTypeItems.get()?.forEach {
                    it.isActivated.set(defaultIdentifiers.contains(it.identifier))
                }
            } else {
                notificationTypeItems.get()?.forEach { it.isActivated.set(true) }
            }
        } else {
            selectedIdentifiers = emptySet()
            defaultIdentifiers = emptySet()
            notificationTypeItems.get()?.forEach {
                it.isActivated.set(false)
            }
        }
    }
}