package dk.shape.games.notifications.bindings

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

inline fun <reified T> Fragment.launch(
    input: T,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline target: suspend T.() -> Unit
): Job {
    return lifecycleScope.launch(dispatcher) {
        target(input)
    }
}

inline fun Fragment.launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline target: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(dispatcher) {
        target(this)
    }
}