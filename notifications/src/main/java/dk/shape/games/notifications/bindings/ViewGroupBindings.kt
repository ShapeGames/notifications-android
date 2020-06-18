package dk.shape.games.notifications.bindings

import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import dk.shape.games.notifications.aliases.ViewProvider

@BindingAdapter("viewProvider")
internal fun ViewGroup.bindViewProvider(viewProvider: ViewProvider?) {
    removeAllViews()
    if (viewProvider != null) {
        addView(viewProvider(context))
    }
}