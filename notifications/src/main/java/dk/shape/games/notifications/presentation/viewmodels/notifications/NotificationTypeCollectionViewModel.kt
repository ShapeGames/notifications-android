package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.value
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding

internal data class NotificationTypeCollectionViewModel(
    private var selectedTypeIds: Set<String>,
    private val defaultTypeIds: Set<String>,
    private val activatedTypeIds: Set<String>,
    private val possibleTypeInfos: Set<NotificationTypeInfo>,
    private val selectionNotifier: (hasSelections: Boolean) -> Unit,
    internal val initialMasterState: Boolean
) {
    val itemBinding = ItemBinding.of<NotificationTypeViewModel>(
        BR.viewModel,
        R.layout.view_notifications_type_item
    )

    val notificationTypeItems: ObservableField<List<NotificationTypeViewModel>> =
        ObservableField(possibleTypeInfos.mapIndexed { index, info ->
            with(info) {
                info.toNotificationTypeViewModel(
                    isLast = index == possibleTypeInfos.size - 1,
                    onNotificationSelected = { isSelected ->
                        if (isSelected) {
                            selectedTypeIds += typeId
                        } else {
                            selectedTypeIds -= typeId
                        }
                        selectionNotifier(selectedTypeIds.isNotEmpty())
                    },
                    isDefault = defaultTypeIds.contains(typeId),
                    isInitialActivated = activatedTypeIds.contains(typeId)
                )
            }
        })

    val hasChanges: Boolean
        get() = activatedTypeIds != selectedTypeIds

    fun resetAll() {
        selectedTypeIds = activatedTypeIds.toSet()
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
                with(viewModel) {
                    if (isActive) {
                        if (defaultTypeIds.isNotEmpty()) {
                            isActivated.awareSet(defaultTypeIds.contains(typeId)) {
                                selectedTypeIds += typeId
                            }
                        } else {
                            isActivated.awareSet(true)
                            selectedTypeIds += typeId
                        }
                    } else {
                        isActivated.awareSet(false)
                        selectedTypeIds -= typeId
                    }
                }
            }
        }
    }
}