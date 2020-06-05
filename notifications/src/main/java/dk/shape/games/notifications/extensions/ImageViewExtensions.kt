package dk.shape.games.notifications.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import dk.shape.danskespil.module.data.entities.Icon
import dk.shape.danskespil.module.getIconResource

@BindingAdapter("icon")
internal fun ImageView.bindIcon(icon: Icon?) {
    val iconRes = icon?.getIconResource(context, false, "", null)
    if (iconRes != null) {
        this.setImageResource(iconRes)
    } else {
        this.setImageDrawable(null)
        this.setImageBitmap(null)
        this.setImageResource(0)
    }
}