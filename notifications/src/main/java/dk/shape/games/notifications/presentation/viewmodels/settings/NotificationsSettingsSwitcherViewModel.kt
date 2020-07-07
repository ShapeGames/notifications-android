package dk.shape.games.notifications.presentation.viewmodels.settings

import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorViewModel
import dk.shape.games.uikit.databinding.UIText
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

sealed class NotificationsSettingsSwitcherViewModel {
    val item: ObservableField<Any> = ObservableField(Loading)

    val itemBinding: ItemBinding<Any> = ItemBinding.of(
        OnItemBindClass<Any>().map(
            Loading::class.java,
            BR.viewModel,
            R.layout.view_notifications_switcher_loading
        ).map(
            Empty::class.java,
            BR.viewModel,
            R.layout.view_notifications_switcher_empty
        ).map(
            Error::class.java,
            BR.viewModel,
            R.layout.view_notifications_switcher_error
        ).map(
            Content::class.java,
            BR.viewModel,
            R.layout.view_notifications_switcher_content
        )
    )

    private val subjectViewModels: List<NotificationsSettingsSubjectViewModel>?
        get() = (item.get() as? Content?)?.items?.filterIsInstance<NotificationsSettingsSubjectViewModel>()

    private val eventViewModels: List<NotificationsSettingsEventViewModel>?
        get() = (item.get() as? Content?)?.items?.filterIsInstance<NotificationsSettingsEventViewModel>()

    object Loading : NotificationsSettingsSwitcherViewModel()
    object Empty : NotificationsSettingsSwitcherViewModel()

    data class Error(
        private val onRetry: () -> Unit
    ) : NotificationsSettingsSwitcherViewModel() {
        val errorViewModel = ErrorViewModel(
            description = UIText.Raw.Resource(R.string.error_description),
            onRetryClick = onRetry
        )
    }

    data class Content(
        val items: List<Any>
    ) : NotificationsSettingsSwitcherViewModel() {
        val itemViews: ItemBinding<Any> = ItemBinding.of(
            OnItemBindClass<Any>().map(
                NotificationsSettingsHeaderViewModel::class.java,
                BR.viewModel,
                R.layout.view_notifications_settings_header
            ).map(
                NotificationsSettingsEventViewModel::class.java,
                BR.viewModel,
                R.layout.view_notifications_settings_event
            ).map(
                NotificationsSettingsSubjectViewModel::class.java,
                BR.viewModel,
                R.layout.view_notifications_settings_subject
            )
        )
    }

    fun setLoading() {
        item.set(Loading)
    }

    fun setEmpty() {
        item.set(Empty)
    }

    fun setError(onRetry: () -> Unit) {
        item.set(Error(onRetry))
    }

    fun setContent(viewModels: List<Any>) {
        item.set(Content(viewModels))
    }

    fun findSubjectViewModel(subjectId: String): NotificationsSettingsSubjectViewModel? =
        subjectViewModels?.find {
            it.subscription.subjectId == subjectId
        }

    fun findEventViewModel(eventId: String): NotificationsSettingsEventViewModel? =
        eventViewModels?.find {
            it.subscription.eventId == eventId
        }
}