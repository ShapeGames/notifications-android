package dk.shape.games.notifications.features.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dk.shape.games.uikit.extensions.dpToPx

internal class NotificationsItemDecoration : RecyclerView.ItemDecoration() {

    companion object {

        @JvmStatic
        fun get(): NotificationsItemDecoration = NotificationsItemDecoration()

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemCount = (parent.adapter?.itemCount ?: 0)
        if (itemCount > 0) {
            val index = parent.getChildAdapterPosition(view)

            if (index == RecyclerView.NO_POSITION) {
                return
            }

            if (index == 0) {
                outRect.top = 20.dpToPx.toInt()
            }
        }
    }
}