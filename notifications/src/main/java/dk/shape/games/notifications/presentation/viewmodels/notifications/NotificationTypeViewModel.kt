package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.NotificationTypeSelected
import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.onChange
import dk.shape.games.notifications.bindings.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal data class NotificationTypeViewModel(
    val name: String,
    val icon: UIImage,
    val isLast: Boolean,
    private val typeId: String,
    private val isDefault: Boolean,
    private val onNotificationTypeSelected: NotificationTypeSelected,
    private val initialState: Boolean
) : ModuleDiffInterface {

    val isEnabled: ObservableBoolean = ObservableBoolean(true)

    val nameItem = ObservableField(initialState.toTypeNameViewModel(name))

    val isActivated: ObservableBoolean = ObservableBoolean(initialState).onChange { value ->
        nameItem.set(value.toTypeNameViewModel(name))
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
            onNotificationTypeSelected(isChecked)
        }
    }

    val onNotificationClicked = View.OnClickListener {
        val newState = !isActivated.get()
        isActivated.awareSet(newState) {
            onNotificationTypeSelected(newState)
        }
    }

    private fun Boolean.toTypeNameViewModel(name: String): SubjectNotificationTypeNameViewModel {
        return when (this) {
            true -> SubjectNotificationTypeNameViewModel.Active(name)
            false -> SubjectNotificationTypeNameViewModel.Normal(name)
        }
    }

    override fun compareContentString() = toString()

    override fun compareString() = typeId
}

internal fun SubjectNotificationType.toNotificationTypeViewModel(
    isLast: Boolean,
    onNotificationTypeSelected: NotificationTypeSelected,
    activatedIdentifiers: Set<SubjectNotificationIdentifier>,
    defaultNofification: Set<SubjectNotificationIdentifier>
) = NotificationTypeViewModel(
    name = name,
    icon = icon.toLocalUIImage(),
    isLast = isLast,
    typeId = identifier.name,
    isDefault = defaultNofification.contains(identifier),
    initialState = activatedIdentifiers.contains(this.identifier),
    onNotificationTypeSelected = onNotificationTypeSelected
)