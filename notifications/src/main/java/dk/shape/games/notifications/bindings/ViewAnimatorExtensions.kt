package dk.shape.games.notifications.bindings

import android.view.View
import android.widget.ViewAnimator
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter

@BindingAdapter("displayedChildId")
internal fun ViewAnimator.setDisplayedChildId(@IdRes displayedChildId: Int?) {
    val childView = displayedChildId?.let { findViewById<View>(it) }
    val childViewIndex = childView?.let { indexOfChild(it) }
    if (displayedChild != childViewIndex) {
        childViewIndex?.let { displayedChild = childViewIndex }
    }
}