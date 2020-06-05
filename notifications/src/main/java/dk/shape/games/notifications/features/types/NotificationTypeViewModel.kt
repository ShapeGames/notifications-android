package dk.shape.games.notifications.features.types

import androidx.lifecycle.*
import dk.shape.games.notifications.usecases.NotificationTypeState
import dk.shape.games.notifications.usecases.NotificationTypeUseCases
import dk.shape.games.notifications.utils.ContentLiveDataEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

internal class NotificationTypeViewModel(
    private val notificationTypeUseCases: NotificationTypeUseCases
) : ViewModel() {

    @ExperimentalCoroutinesApi
    private val state = notificationTypeUseCases.state.asLiveData()

    data class ToggleData(
        val notificationTypeId: String,
        val enabled: Boolean
    )

    private val mutableEnabled = MutableLiveData<Boolean>()
    val enabled: LiveData<Boolean> = mutableEnabled

    private val mutableName = MutableLiveData<String>()
    val name: LiveData<String> = mutableName

    private val _toggleEvent = MutableLiveData<ContentLiveDataEvent<ToggleData>>()
    val toggleEvent: LiveData<ContentLiveDataEvent<ToggleData>> = _toggleEvent

    init {
        state.observeForever { setState(it) }
        load()
    }

    private fun load() = viewModelScope.launch {
        notificationTypeUseCases.loadNotificationType()
    }

    @ExperimentalCoroutinesApi
    fun onToggled(enabled: Boolean) = synchronized(this.enabled) {
        (state.value as? NotificationTypeState.Content)?.let {
            if(this.enabled.value != enabled && this.enabled.value != null) {
                _toggleEvent.postValue(
                    ContentLiveDataEvent(ToggleData(it.notificationTypeId, enabled))
                )
            }
        }
    }

    private fun setState(state: NotificationTypeState) = when (state) {
        is NotificationTypeState.Content -> {
            mutableEnabled.postValue(state.enabled)
            mutableName.postValue(state.name)
        }
    }

    override fun equals(other: Any?): Boolean = if(other is NotificationTypeViewModel) {
        other.state.value == this.state.value
    } else false

}