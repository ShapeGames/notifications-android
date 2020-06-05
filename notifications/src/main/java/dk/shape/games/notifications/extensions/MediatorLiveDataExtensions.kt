package dk.shape.games.notifications.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

internal fun <T : Any> MediatorLiveData<T>.updateSources(
    oldSources: List<LiveData<T>>?,
    newSources: List<LiveData<T>>?,
    onChanged: Observer<in T>
) {
    oldSources?.filterNot { newSources?.contains(it) == true }?.forEach {
        this.removeSource(it)
    }
    newSources?.filterNot { oldSources?.contains(it) == true }?.forEach {
        this.addSource(it, onChanged)
    }
}