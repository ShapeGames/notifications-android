package dk.shape.games.notifications.bindings

import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("onStateChange")
internal fun AppCompatCheckBox.onStateChange(onStateChange: CompoundButton.OnCheckedChangeListener) {
    this.setOnCheckedChangeListener(onStateChange)
}

@BindingAdapter("onStateChange")
internal fun SwitchCompat.onStateChange(onStateChange: CompoundButton.OnCheckedChangeListener) {
    this.setOnCheckedChangeListener(onStateChange)
}
