package dk.shape.games.notifications.extensions

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

@Suppress("UNCHECKED_CAST")
@BindingAdapter(value = ["items", "itemBinding"], requireAll = false)
internal fun <T : ModuleDiffInterface> LinearLayout.setAdapter(
    items: List<T>?,
    itemBinding: ItemBinding<T>?
) {
    if (itemBinding == null) return

    LinearLayoutBindingCollectionAdapter(
        linearLayout = this,
        bindings = itemBinding
    ).apply {
        setItemBinding(itemBinding)
        setItems(items)
    }
}