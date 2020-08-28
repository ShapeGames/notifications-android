package dk.shape.games.notifications.extensions

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("itemDecoration")
internal fun RecyclerView.bindItemDecoration(decoration: RecyclerView.ItemDecoration?) {
    decoration?.let {
        if (itemDecorationCount > 0) {
            removeItemDecorationAt(0)
        }

        addItemDecoration(it, 0)
    }
}