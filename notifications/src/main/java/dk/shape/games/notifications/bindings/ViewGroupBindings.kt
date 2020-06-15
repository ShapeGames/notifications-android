package dk.shape.games.notifications.bindings

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.utils.LinearLayoutBindingCollectionAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

@BindingAdapter("viewProvider")
internal fun ViewGroup.bindViewProvider(viewProvider: ViewProvider?) {
    removeAllViews()
    if (viewProvider != null) {
        addView(viewProvider(context))
    }
}