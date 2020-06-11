package dk.shape.games.notifications.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dk.shape.danskespil.module.ui.ModuleDiffInterface
import me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

class LinearLayoutBindingCollectionAdapter<T : ModuleDiffInterface>(
    private val linearLayout: LinearLayout,
    private var bindings: ItemBinding<in T>
) : BindingCollectionAdapter<T> {

    private var items: MutableList<T> = mutableListOf()
    private var inflater: LayoutInflater = LayoutInflater.from(linearLayout.context)

    override fun setItemBinding(itemBinding: ItemBinding<in T>) {
        this.bindings = itemBinding
    }

    override fun getItemBinding(): ItemBinding<in T> {
        return bindings
    }

    override fun setItems(items: List<T>?) {
        requireNotNull(items) { "items must not be null" }
        this.items = items.toMutableList()
        createViews()
    }

    private fun getBinding(item: T, position: Int): ViewDataBinding {
        return DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getItemViewType(position, item),
            linearLayout,
            false
        ).apply {
            onBindBinding(this, itemBinding.variableId(), itemBinding.layoutRes(), position, item)
            executePendingBindings()
        }
    }

    private fun createViews() {
        linearLayout.removeAllViews()
        for ((i, item) in items.withIndex()) {
            val binding = onCreateBinding(inflater, getItemViewType(i, item), linearLayout)
            onBindBinding(binding, itemBinding.variableId(), itemBinding.layoutRes(), i, item)
            binding.executePendingBindings()
        }
    }

    override fun getAdapterItem(position: Int): T? {
        return items.getOrNull(position)
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        @LayoutRes layoutId: Int,
        viewGroup: ViewGroup
    ): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, layoutId, viewGroup, true)
    }

    override fun onBindBinding(
        binding: ViewDataBinding,
        bindingVariable: Int,
        @LayoutRes layoutRes: Int,
        position: Int,
        item: T
    ) {
        if (itemBinding.bind(binding, item)) {
            binding.executePendingBindings()
        }
    }

    private fun getItemViewType(position: Int, items: T): Int {
        itemBinding.onItemBind(position, items)
        return itemBinding.layoutRes()
    }
}