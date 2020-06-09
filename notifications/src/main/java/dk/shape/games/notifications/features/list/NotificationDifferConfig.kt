package dk.shape.games.notifications.features.list

import androidx.recyclerview.widget.DiffUtil

internal class NotificationDifferConfig : DiffUtil.ItemCallback<EventNotificationViewModel>() {
    override fun areItemsTheSame(
        oldItem: EventNotificationViewModel,
        newItem: EventNotificationViewModel
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: EventNotificationViewModel,
        newItem: EventNotificationViewModel
    ): Boolean {
        return oldItem == newItem
    }

}