package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.OnNotificationTypeSelected
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.onChange
import dk.shape.games.uikit.databinding.UIImage
import dk.shape.games.uikit.utils.UIDiffInterface
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal data class NotificationTypeViewModel(
    val name: String,
    val icon: UIImage,
    val typeId: String,
    val isLast: Boolean,
    private val isDefault: Boolean,
    private val isInitialActivated: Boolean,
    private val onNotificationSelected: OnNotificationTypeSelected
) : UIDiffInterface {

    val isEnabled: ObservableBoolean = ObservableBoolean(true)

    val nameItem = ObservableField(isInitialActivated.toTypeNameViewModel(name))

    val isActivated: ObservableBoolean = ObservableBoolean(isInitialActivated).onChange { value ->
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
            onNotificationSelected(isChecked)
        }
    }

    val onNotificationClicked = View.OnClickListener {
        val newState = !isActivated.get()
        isActivated.awareSet(newState) {
            onNotificationSelected(newState)
        }
    }

    private fun Boolean.toTypeNameViewModel(name: String): SubjectNotificationTypeNameViewModel {
        return when (this) {
            true -> SubjectNotificationTypeNameViewModel.Active(name)
            false -> SubjectNotificationTypeNameViewModel.Normal(name)
        }
    }

    override val id = typeId
}

internal fun NotificationTypeInfo.toNotificationTypeViewModel(
    isLast: Boolean,
    isDefault: Boolean,
    isInitialActivated: Boolean,
    onNotificationSelected: OnNotificationTypeSelected
) = NotificationTypeViewModel(
    name = name,
    icon = icon ?: UIImage.gone(),
    typeId = typeId,
    isLast = isLast,
    isDefault = isDefault,
    isInitialActivated = isInitialActivated,
    onNotificationSelected = onNotificationSelected
)



