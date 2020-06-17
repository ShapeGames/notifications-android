package dk.shape.games.notifications.bindings

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.utils.LinearLayoutBindingCollectionAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

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