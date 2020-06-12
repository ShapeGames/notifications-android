package dk.shape.games.notifications.extensions

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

inline fun <reified R, T> ObservableField<T>.value(target: T.() -> R): R? {
    val value = this.get()
    return if (value != null) {
        target(value)
    } else null
}

fun ObservableBoolean.awareSet(value: Boolean, onChange: (() -> Unit)? = null) {
    if (get() != value) {
        set(value)
        onChange?.invoke()
    }
}