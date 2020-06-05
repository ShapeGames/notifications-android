package dk.shape.games.notifications.extensions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visible")
internal fun View.setVisible(visible: Boolean?) {
    this.visibility = if (visible == true) View.VISIBLE else View.GONE
}