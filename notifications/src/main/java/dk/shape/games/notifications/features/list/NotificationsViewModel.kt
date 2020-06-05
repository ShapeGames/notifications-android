package dk.shape.games.notifications.features.list

import android.view.View
import androidx.lifecycle.*
import androidx.recyclerview.widget.AsyncDifferConfig
import dk.shape.games.notifications.BR
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.extensions.ItemsCreator
import dk.shape.games.notifications.extensions.updateSources
import dk.shape.games.notifications.usecases.NotificationsState
import dk.shape.games.notifications.usecases.NotificationsUseCases
import dk.shape.games.notifications.utils.ContentLiveDataEvent
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.OnItemBind

internal class NotificationsViewModel(
    private val notificationsUseCases: NotificationsUseCases,
    val loadingViewProvider: ViewProvider, // public to be accessible through data binding
    private val provideNotificationViewModel: (event: Event) -> NotificationViewModel
) : ViewModel() {

    @ExperimentalCoroutinesApi
    private val state = notificationsUseCases.state.asLiveData()

    private val mutableStateViewId = MutableLiveData<Int>()
    val stateViewId: LiveData<Int> = mutableStateViewId

    private var currentNotificationViewModelSource: List<NotificationViewModel>? = null
        set(value) {
            mutableConfigurationEvent.updateSources(
                field?.map { it.configurationEvent },
                value?.map { it.configurationEvent },
                Observer { mutableConfigurationEvent.postValue(it) }
            )
            field = value
        }
    private val mutableNotificationViewModelsCreator =
        MutableLiveData<ItemsCreator<NotificationViewModel>>()
    val notificationViewModelsCreator: LiveData<ItemsCreator<NotificationViewModel>> =
        mutableNotificationViewModelsCreator

    val notificationsBinding: OnItemBind<NotificationViewModel> =
        OnItemBind { itemBinding, position, item ->
            itemBinding.set(BR.viewModel, R.layout.view_notification)
        }

    val notificationsDiffConfig =
        AsyncDifferConfig.Builder<NotificationViewModel>(NotificationDifferConfig()).build()

    private val mutableConfigurationEvent = MediatorLiveData<ContentLiveDataEvent<String>>()
    val configurationEvent: LiveData<ContentLiveDataEvent<String>> = mutableConfigurationEvent

    val errorMessage = R.string.general_noConnection_title
    val errorIcon = R.drawable.icon_network_error_grey
    val errorButtonText = R.string.general_tryAgain_button
    val onErrorButtonClicked = View.OnClickListener { v: View? -> loadSubscriptions() }

    init {
        state.observeForever { setState(it) }
        loadSubscriptions()
    }

    private fun setState(state: NotificationsState) {
        if (state is NotificationsState.Content) {
            mutableNotificationViewModelsCreator.postValue(
                ItemsCreator {
                    state.events.map {
                        provideNotificationViewModel(it)
                    }.also {
                        currentNotificationViewModelSource = it
                    }
                }
            )
        }
        mutableStateViewId.postValue(
            when (state) {
                is NotificationsState.Content -> R.id.layout_content
                is NotificationsState.Loading -> R.id.layout_loading
                is NotificationsState.Error -> R.id.layout_error
                is NotificationsState.Empty -> R.id.layout_empty
            }
        )
    }

    private fun loadSubscriptions() = viewModelScope.launch {
        notificationsUseCases.loadNotifications()
    }

}