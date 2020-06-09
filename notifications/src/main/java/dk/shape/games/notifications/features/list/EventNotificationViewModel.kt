package dk.shape.games.notifications.features.list

import androidx.lifecycle.*
import dk.shape.games.notifications.usecases.EventNotificationState
import dk.shape.games.notifications.usecases.NotificationUseCases
import dk.shape.games.notifications.utils.ContentLiveDataEvent
import kotlinx.coroutines.*

internal data class EventNotificationViewModel constructor(
    val notificationUseCases: NotificationUseCases
) : ViewModel() {

    @ExperimentalCoroutinesApi
    private val state = notificationUseCases.state.asLiveData()

    private val mutableEventNameLine1 = MutableLiveData<String>()
    val eventNameLine1: LiveData<String> = mutableEventNameLine1

    private val mutableEventNameLine2 = MutableLiveData<String>()
    val eventNameLine2: LiveData<String> = mutableEventNameLine2

    private val mutableActiveSubscriptionsText = MutableLiveData<String>()
    val activeSubscriptionsText: LiveData<String> = mutableActiveSubscriptionsText

    private val mutableEnabled = MutableLiveData<Boolean>()
    val enabled: LiveData<Boolean> = mutableEnabled

    private val mutableConfigurationEvent = MutableLiveData<ContentLiveDataEvent<String>>()
    val configurationEvent: LiveData<ContentLiveDataEvent<String>> = mutableConfigurationEvent

    init {
        state.observeForever { setContentState(it.toContent()) }
        load()
    }

    private fun load() = viewModelScope.launch {
        notificationUseCases.loadNotification()
    }

    @ExperimentalCoroutinesApi
    fun onToggled(enabled: Boolean)  {
        if (this.enabled.value != enabled && this.enabled.value != null) {
            (GlobalScope + SupervisorJob()).launch {
                notificationUseCases.toggle(enabled)
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun onConfigureClicked() {
        state.value?.toContent()?.eventId?.let {
            mutableConfigurationEvent.postValue(ContentLiveDataEvent(it))
        }
    }

    private fun setContentState(contentState: EventNotificationState.Content) {
        mutableEventNameLine1.postValue(contentState.eventNameLine1)
        mutableEventNameLine2.postValue(contentState.eventNameLine2)
        mutableActiveSubscriptionsText.postValue(
            contentState.enabledNotificationTypeNames.joinToString(", ")
        )
        mutableEnabled.postValue(contentState.isEnabled())
    }

    private fun EventNotificationState.toContent(): EventNotificationState.Content = when(this) {
        is EventNotificationState.Content -> this
        is EventNotificationState.Error -> lastKnownState
    }

    override fun equals(other: Any?): Boolean = if(other is EventNotificationViewModel) {
        other.state.value == this.state.value
    } else false

}