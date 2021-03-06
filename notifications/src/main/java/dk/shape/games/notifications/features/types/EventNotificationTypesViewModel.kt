package dk.shape.games.notifications.features.types

import androidx.lifecycle.*
import androidx.recyclerview.widget.AsyncDifferConfig
import dk.shape.danskespil.module.data.entities.Icon
import dk.shape.games.feedbackui.FeedbackInfoViewModel
import dk.shape.games.notifications.BR
import dk.shape.games.notifications.R
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.bindings.ItemsCreator
import dk.shape.games.notifications.usecases.EventNotificationTypesState
import dk.shape.games.notifications.usecases.EventNotificationTypesUseCases
import dk.shape.games.notifications.utils.ContentLiveDataEvent
import dk.shape.games.toolbox_library.utils.RelativeDateUtils
import dk.shape.games.uikit.databinding.UIImage
import dk.shape.games.uikit.databinding.UIText
import kotlinx.coroutines.*
import me.tatarka.bindingcollectionadapter2.OnItemBind

internal class EventNotificationTypesViewModel(
    private val useCases: EventNotificationTypesUseCases,
    val loadingViewProvider: ViewProvider, // public to be accessible through data binding
    private val createNotificationTypeViewModel: (eventId: String, notificationTypeId: String) -> NotificationTypeViewModel
) : ViewModel() {

    @ExperimentalCoroutinesApi
    private val state = useCases.state.asLiveData()

    private val mutableStateViewId = MutableLiveData<Int>()
    val stateViewId: LiveData<Int> = mutableStateViewId

    private val mutableEventNameLine1 = MutableLiveData<String>()
    val eventNameLine1: LiveData<String> = mutableEventNameLine1

    private val mutableEventNameLine2 = MutableLiveData<String>()
    val eventNameLine2: LiveData<String> = mutableEventNameLine2

    private val mutableEventStartTime = MutableLiveData<UIText>()
    val eventStartTime: LiveData<UIText> = mutableEventStartTime

    private val mutableEventStartDate = MutableLiveData<UIText>()
    val eventStartDate: LiveData<UIText> = mutableEventStartDate

    private val mutableEventIcon = MutableLiveData<Icon>()
    val eventIcon: LiveData<Icon> = mutableEventIcon

    private val mutableLevel2Name = MutableLiveData<String>()
    val level2Name: LiveData<String> = mutableLevel2Name

    private val mutableLevel3Name = MutableLiveData<String>()
    val level3Name: LiveData<String> = mutableLevel3Name

    val errorFeedbackViewModel = FeedbackInfoViewModel.ScreenError(
        customTitleText = UIText.Raw.Resource(R.string.offerings_general_noConnection_title),
        onScreenRetry = { loadNotificationTypes() }
    )

    private var notificationTypeViewModelsSource: List<NotificationTypeViewModel>? = null
        set(value) {
            field?.forEach {
                if (value?.contains(it) != true) it.toggleEvent.removeObserver(
                    notificationTypeToggleObserver
                )
            }
            value?.forEach {
                if (field?.contains(it) != true) it.toggleEvent.observeForever(
                    notificationTypeToggleObserver
                )
            }
            field = value
        }
    private val _notificationTypeViewModelsCreator =
        MutableLiveData<ItemsCreator<NotificationTypeViewModel>>()
    val notificationTypeViewModelsCreator: LiveData<ItemsCreator<NotificationTypeViewModel>> =
        _notificationTypeViewModelsCreator
    val notificationTypesBinding: OnItemBind<NotificationTypeViewModel> =
        OnItemBind { itemBinding, position, item ->
            val itemCount = notificationTypeViewModelsSource?.size ?: 0
            if (itemCount == 1) {
                itemBinding.set(BR.viewModel, R.layout.view_notification_type_bg_single)
            } else if (position == 0) {
                itemBinding.set(BR.viewModel, R.layout.view_notification_type_bg_first)
            } else if (position == itemCount - 1) {
                itemBinding.set(BR.viewModel, R.layout.view_notification_type_bg_last)
            } else {
                itemBinding.set(BR.viewModel, R.layout.view_notification_type_bg_middle)
            }
        }
    val notificationTypesDiffConfig =
        AsyncDifferConfig.Builder<NotificationTypeViewModel>(NotificationTypeDifferConfig()).build()

    private val notificationTypeToggleObserver =
        Observer<ContentLiveDataEvent<NotificationTypeViewModel.ToggleData>> { eventContent ->
            eventContent.getContentIfNotHandled()?.let { toggleData ->
                toggleNotification(toggleData.notificationTypeId, toggleData.enabled)
            }
        }

    init {
        state.observeForever { setState(it) }
        loadNotificationTypes()
    }

    private fun setState(state: EventNotificationTypesState) = when (state) {
        is EventNotificationTypesState.Content -> {
            mutableEventNameLine1.postValue(state.eventNameLine1)
            mutableEventNameLine2.postValue(state.eventNameLine2)
            mutableEventStartDate.postValue(
                state.eventStartDate?.let {
                    UIText.ByContext { context ->
                        UIText.Raw.String(
                            RelativeDateUtils.getRelativeDateString(context, it, false)
                        )
                    }
                }
            )
            mutableEventStartTime.postValue(
                state.eventStartDate?.let {
                    UIText.ByContext { context ->
                        UIText.Raw.String(RelativeDateUtils.getTimeString(context, it))
                    }
                }
            )
            mutableEventIcon.postValue(state.eventIcon)
            mutableLevel2Name.postValue(state.level2Name)
            mutableLevel3Name.postValue(state.level3Name)
            _notificationTypeViewModelsCreator.postValue(
                ItemsCreator {
                    state.notificationTypesIds.map {
                        createNotificationTypeViewModel(state.eventId, it)
                    }.also {
                        notificationTypeViewModelsSource = it
                    }
                }
            )
            mutableStateViewId.postValue(R.id.layout_content)
        }
        is EventNotificationTypesState.Loading -> {
            mutableStateViewId.postValue(R.id.layout_loading)
        }
        is EventNotificationTypesState.Error -> {
            mutableStateViewId.postValue(R.id.layout_error)
        }
    }

    private fun loadNotificationTypes() = viewModelScope.launch {
        useCases.loadNotificationTypes()
    }

    private fun toggleNotification(notificationTypeId: String, enabled: Boolean) =
        (GlobalScope + SupervisorJob()).launch {
            useCases.toggleNotificationType(notificationTypeId, enabled)
        }

}