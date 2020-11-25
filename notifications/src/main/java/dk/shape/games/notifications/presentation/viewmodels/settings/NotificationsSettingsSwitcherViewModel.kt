package dk.shape.games.notifications.presentation.viewmodels.settings

import androidx.databinding.ObservableField
import dk.shape.games.feedbackui.FeedbackInfoViewModel
import dk.shape.games.feedbackui.FeedbackLoadingViewModel
import dk.shape.games.notifications.R
import dk.shape.games.uikit.databinding.UIImage
import dk.shape.games.uikit.databinding.UIText
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class NotificationsSettingsSwitcherViewModel {
    val item: ObservableField<Any> = ObservableField(FeedbackLoadingViewModel)

    val itemBinding: ItemBinding<Any> = ItemBinding.of(
        OnItemBindClass<Any>()
            .map(
                FeedbackLoadingViewModel::class.java,
                BR.viewModel,
                R.layout.feedback_loading_vm
            )
            .map(
                FeedbackInfoViewModel::class.java,
                BR.viewModel,
                R.layout.feedback_info
            )
            .map(
                Content::class.java,
                BR.viewModel,
                R.layout.view_notifications_switcher_content
            )
    )

    private val subjectViewModels: List<NotificationsSettingsSubjectViewModel>?
        get() = (item.get() as? Content?)?.items?.filterIsInstance<NotificationsSettingsSubjectViewModel>()

    private val eventViewModels: List<NotificationsSettingsEventViewModel>?
        get() = (item.get() as? Content?)?.items?.filterIsInstance<NotificationsSettingsEventViewModel>()

    private val emptyViewModel = FeedbackInfoViewModel.ScreenEmpty(
        customIcon = UIImage.Raw.Resource(R.drawable.ic_notification_big),
        customTitleText = UIText.Raw.Resource(R.string.notification_settings_empty_title),
        customDescriptionText = UIText.Raw.Resource(R.string.notification_settings_empty_description),
        isLight = true
    )

    data class Content(
        val items: List<Any>
    ) {
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
        item.set(FeedbackLoadingViewModel)
    }

    fun setEmpty() {
        item.set(emptyViewModel)
    }

    fun setError(onRetry: () -> Unit) {
        item.set(getErrorViewModel(onRetry))
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

    private fun getErrorViewModel(onRetry: () -> Unit): FeedbackInfoViewModel.ScreenError =
        FeedbackInfoViewModel.ScreenError(
            customDescriptionText = UIText.Raw.Resource(R.string.error_description),
            onScreenRetry = onRetry
        )
}