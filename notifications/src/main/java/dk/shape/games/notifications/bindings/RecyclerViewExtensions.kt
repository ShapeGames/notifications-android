package dk.shape.games.notifications.bindings

import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapters
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * The point of this binding is to allow us using binding collection adapter in such a way, that
 * items are created only when they are really needed (when bindings are executed) and to
 * make sure that the items are only created when view actually exists (we know that view exists
 * when this custom binding is executed).
 *
 * For example, assume list of notifications where each notification is represented by a view model
 * that is constructed from a [ViewModelStore] of a fragment. If this list of view models is created
 * whenever repository emits new notifications, we might end up creating new notification view
 * models while the fragment is detached, which results in a crash. However, if we create only
 * items creator when new notifications are emitted, we can make sure that these creator is
 * invoked only when the data is actually bind to the view, and view has to exists at that point
 * in time (and fragment will be attached). This custom binder allows us to do that.
 */
@BindingAdapter(
    value = ["itemBinding", "itemsCreator", "adapter", "itemIds", "viewHolder", "diffConfig"],
    requireAll = false
)
internal fun <T> RecyclerView.setAdapter(
    itemBinding: ItemBinding<in T>?,
    itemsCreator: ItemsCreator<T>?,
    adapter: BindingRecyclerViewAdapter<T>?,
    itemIds: BindingRecyclerViewAdapter.ItemIds<in T>?,
    viewHolderFactory: BindingRecyclerViewAdapter.ViewHolderFactory?,
    diffConfig: AsyncDifferConfig<T>?
) {
    BindingRecyclerViewAdapters.setAdapter(
        this,
        itemBinding,
        itemsCreator?.createItems?.invoke(),
        adapter,
        itemIds,
        viewHolderFactory,
        diffConfig
    )
}

internal data class ItemsCreator<T>(val createItems: () -> List<T>)