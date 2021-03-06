package dk.shape.games.notifications.features.list

import androidx.lifecycle.*
import androidx.recyclerview.widget.AsyncDifferConfig
import dk.shape.games.feedbackui.FeedbackInfoViewModel
import dk.shape.games.notifications.BR
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.bindings.ItemsCreator
import dk.shape.games.notifications.bindings.updateSources
import dk.shape.games.notifications.usecases.EventNotificationsState
import dk.shape.games.notifications.usecases.EventNotificationsUseCases
import dk.shape.games.notifications.utils.ContentLiveDataEvent
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.uikit.databinding.UIText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.OnItemBind

internal class EventNotificationsViewModel(
    private val notificationsUseCases: EventNotificationsUseCases,
    val loadingViewProvider: ViewProvider, // public to be accessible through data binding
    private val provideNotificationViewModel: (event: Event) -> EventNotificationViewModel
) : ViewModel() {

    @ExperimentalCoroutinesApi
    private val state = notificationsUseCases.state.asLiveData()

    private val mutableStateViewId = MutableLiveData<Int>()
    val stateViewId: LiveData<Int> = mutableStateViewId

    val errorFeedbackViewModel = FeedbackInfoViewModel.ScreenError(
        customTitleText = UIText.Raw.Resource(R.string.offerings_general_noConnection_title),
        onScreenRetry = { loadSubscriptions() }
    )

    private var currentNotificationViewModelSource: List<EventNotificationViewModel>? = null
        set(value) {
            mutableConfigurationEvent.updateSources(
                field?.map { it.configurationEvent },
                value?.map { it.configurationEvent },
                Observer { mutableConfigurationEvent.postValue(it) }
            )
            field = value
        }
    private val mutableNotificationViewModelsCreator =
        MutableLiveData<ItemsCreator<EventNotificationViewModel>>()
    val notificationViewModelsCreator: LiveData<ItemsCreator<EventNotificationViewModel>> =
        mutableNotificationViewModelsCreator

    val notificationsBinding: OnItemBind<EventNotificationViewModel> =
        OnItemBind { itemBinding, position, item ->
            itemBinding.set(BR.viewModel, R.layout.view_event_notification)
        }

    val notificationsDiffConfig =
        AsyncDifferConfig.Builder<EventNotificationViewModel>(NotificationDifferConfig()).build()

    private val mutableConfigurationEvent = MediatorLiveData<ContentLiveDataEvent<String>>()
    val configurationEvent: LiveData<ContentLiveDataEvent<String>> = mutableConfigurationEvent

    init {
        state.observeForever { setState(it) }
        loadSubscriptions()
    }

    private fun setState(state: EventNotificationsState) {
        if (state is EventNotificationsState.Content) {
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
                is EventNotificationsState.Content -> R.id.layout_content
                is EventNotificationsState.Loading -> R.id.layout_loading
                is EventNotificationsState.Error -> R.id.layout_error
                is EventNotificationsState.Empty -> R.id.layout_empty
            }
        )
    }

    private fun loadSubscriptions() = viewModelScope.launch {
        notificationsUseCases.loadNotifications()
    }

}