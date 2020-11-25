package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.feedbackui.FeedbackInfoViewModel
import dk.shape.games.feedbackui.FeedbackLoadingViewModel
import dk.shape.games.notifications.R
import dk.shape.games.notifications.bindings.onChange
import dk.shape.games.uikit.databinding.UIText
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal class EventNotificationSwitcherViewModel(
    initialContentItem: NotificationSheetEventViewModel?,
    val onItemChanged: () -> Unit
) {
    val itemBinding: ItemBinding<Any> = ItemBinding.of(
        OnItemBindClass<Any>()
            .map(FeedbackInfoViewModel::class.java, BR.viewModel, R.layout.feedback_info)
            .map(FeedbackLoadingViewModel::class.java, BR.viewModel, R.layout.feedback_loading_vm)
            .map(
                NotificationSheetEventViewModel::class.java,
                BR.viewModel,
                R.layout.view_notification_sheet_event
            )
    )

    val item: ObservableField<Any> = ObservableField(
        initialContentItem ?: FeedbackLoadingViewModel
    ).onChange {
        onItemChanged()
    }

    fun showContent(viewModel: NotificationSheetEventViewModel) {
        item.set(viewModel)
    }

    fun showLoading() {
        item.set(FeedbackLoadingViewModel)
    }

    fun showError(onRetryClick: () -> Unit) {
        item.set(
            getErrorViewModel(onRetryClick)
        )
    }

    private fun getErrorViewModel(
        onRetry: () -> Unit
    ): FeedbackInfoViewModel.ModuleError = FeedbackInfoViewModel.ModuleError(
        customTitleText = UIText.Raw.Resource(R.string.error_title),
        onModuleRetry = onRetry
    )
}
