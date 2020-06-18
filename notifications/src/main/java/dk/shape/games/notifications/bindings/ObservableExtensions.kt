package dk.shape.games.notifications.bindings

import androidx.databinding.Observable
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

fun ObservableBoolean.onChange(listener: (value: Boolean) -> Unit): ObservableBoolean {
    this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            listener(get())
        }
    })
    return this
}

inline fun <reified T> ObservableField<T>.onChange(crossinline listener: (value: T) -> Unit): ObservableField<T> {
    this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            listener(get() as T)
        }
    })
    return this
}