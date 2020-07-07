package dk.shape.games.notifications.presentation.viewmodels.notifications

import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.notifications.bindings.onChange
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.LoadingViewModel
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal class SubjectNotificationSwitcherViewModel(
    initialContentItem: NotificationSheetSubjectViewModel?,
    val onItemChanged: () -> Unit
) {
    private val loadingViewModel = LoadingViewModel()

    val itemBinding: ItemBinding<Any> = ItemBinding.of(
        OnItemBindClass<Any>()
            .map(ErrorViewModel::class.java, BR.viewModel, R.layout.state_error_view)
            .map(LoadingViewModel::class.java, BR.viewModel, R.layout.state_loading_view)
            .map(
                NotificationSheetSubjectViewModel::class.java,
                BR.viewModel,
                R.layout. view_notification_sheet_subject
            )
    )

    val item: ObservableField<Any> = ObservableField<Any>(
        initialContentItem ?: loadingViewModel
    ).onChange {
        onItemChanged()
    }

    fun showContent(viewModel: NotificationSheetSubjectViewModel) {
        item.set(viewModel)
    }

    fun showLoading() {
        item.set(loadingViewModel)
    }

    fun showError(onRetryClick: () -> Unit) {
        item.set(
            ErrorViewModel(
                onRetryClick = onRetryClick
            )
        )
    }
}
