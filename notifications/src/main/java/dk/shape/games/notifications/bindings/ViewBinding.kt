package dk.shape.games.notifications.bindings

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter

@BindingAdapter("marginTopRes")
fun View.setMarginTopRes(marginTopRes: Int) {
    val marginTop: Float = resources.getDimension(marginTopRes)

    (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(
            leftMargin,
            marginTop.toInt(),
            rightMargin,
            bottomMargin
        )
    }?.also {
        layoutParams = it
    }
}