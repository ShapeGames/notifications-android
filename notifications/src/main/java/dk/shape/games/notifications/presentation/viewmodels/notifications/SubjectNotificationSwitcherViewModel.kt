package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dk.shape.games.notifications.R
import dk.shape.games.notifications.bindings.onChange
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.LoadingViewModel
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

internal class SubjectNotificationSwitcherViewModel {

    private val loadingViewModel = LoadingViewModel()
    var bottomSheet: ViewGroup? = null

    val itemBinding: ItemBinding<Any> = ItemBinding.of(
        OnItemBindClass<Any>()
            .map(ErrorViewModel::class.java, BR.viewModel, R.layout.state_error_view)
            .map(LoadingViewModel::class.java, BR.viewModel, R.layout.state_loading_view)
            .map(
                SubjectNotificationViewModel::class.java,
                BR.viewModel,
                R.layout.view_subject_notifications
            )
    )

    val item: ObservableField<Any> = ObservableField(loadingViewModel)

    init {
        item.onChange {
            bottomSheet?.let {
                TransitionManager.beginDelayedTransition(
                    it,
                    AutoTransition().setInterpolator(FastOutSlowInInterpolator())
                )
            }
        }
    }

    fun showContent(viewModel: SubjectNotificationViewModel) {
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
