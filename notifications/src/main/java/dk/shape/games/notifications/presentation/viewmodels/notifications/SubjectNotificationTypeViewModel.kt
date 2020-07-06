package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.SelectionStateNotifier
import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.onChange
import dk.shape.games.notifications.bindings.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal data class SubjectNotificationTypeViewModel(
    val icon: UIImage,
    val identifier: SubjectNotificationIdentifier,
    val notificationTypeName: String,
    val isLastElement: Boolean,
    private val isDefault: Boolean,
    private val stateNotifier: SelectionStateNotifier,
    private val initialState: Boolean
) : ModuleDiffInterface {

    val isEnabled: ObservableBoolean = ObservableBoolean(true)

    val nameItem = ObservableField(initialState.toTypeNameViewModel(notificationTypeName))

    val isActivated: ObservableBoolean = ObservableBoolean(initialState).onChange { value ->
        nameItem.set(value.toTypeNameViewModel(notificationTypeName))
    }

    val nameItemBinding: ItemBinding<Any> = ItemBinding.of(
        OnItemBindClass<Any>()
            .map(
                SubjectNotificationTypeNameViewModel.Normal::class.java,
                BR.viewModel,
                R.layout.view_subject_notifications_type_item_name
            )
            .map(
                SubjectNotificationTypeNameViewModel.Active::class.java,
                BR.viewModel,
                R.layout.view_subject_notifications_type_item_name_active
            )
    )

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated.awareSet(isChecked) {
            stateNotifier(isChecked, identifier)
        }
    }

    val onNotificationClicked = View.OnClickListener {
        val newState = !isActivated.get()
        isActivated.awareSet(newState) {
            stateNotifier(newState, identifier)
        }
    }

    private fun Boolean.toTypeNameViewModel(name: String): SubjectNotificationTypeNameViewModel {
        return when (this) {
            true -> SubjectNotificationTypeNameViewModel.Active(name)
            false -> SubjectNotificationTypeNameViewModel.Normal(name)
        }
    }

    override fun compareContentString() = toString()
    override fun compareString() = identifier.name
}

internal fun SubjectNotificationType.toNotificationTypeViewModel(
    isLastElement: Boolean,
    selectionStateNotifier: SelectionStateNotifier,
    activatedIdentifiers: Set<SubjectNotificationIdentifier>,
    defaultNofification: Set<SubjectNotificationIdentifier>
) = SubjectNotificationTypeViewModel(
    icon = icon.toLocalUIImage(),
    identifier = identifier,
    isDefault = defaultNofification.contains(identifier),
    initialState = activatedIdentifiers.contains(this.identifier),
    stateNotifier = selectionStateNotifier,
    isLastElement = isLastElement,
    notificationTypeName = name
)