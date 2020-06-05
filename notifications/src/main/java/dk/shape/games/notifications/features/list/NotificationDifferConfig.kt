package dk.shape.games.notifications.features.list

import androidx.recyclerview.widget.DiffUtil

internal class NotificationDifferConfig : DiffUtil.ItemCallback<NotificationViewModel>() {
    override fun areItemsTheSame(
        oldItem: NotificationViewModel,
        newItem: NotificationViewModel
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: NotificationViewModel,
        newItem: NotificationViewModel
    ): Boolean {
        return oldItem == newItem
    }

}