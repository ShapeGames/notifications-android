package dk.shape.games.notifications.bindings

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

inline fun <reified R, T> ObservableField<T>.value(target: T.() -> R): R? {
    val value = this.get()
    return if (value != null) {
        target(value)
    } else null
}

inline fun <reified R, T : Any> ObservableField<T>.requireValue(target: T.() -> R): R {
    val value = this.get()
    requireNotNull(value)
    return target(value)
}

fun ObservableBoolean.awareSet(value: Boolean, onChange: (() -> Unit)? = null) {
    if (get() != value) {
        set(value)
        onChange?.invoke()
    }
}