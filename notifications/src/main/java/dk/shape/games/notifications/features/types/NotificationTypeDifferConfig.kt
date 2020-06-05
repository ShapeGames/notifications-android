package dk.shape.games.notifications.features.types

import androidx.recyclerview.widget.DiffUtil

internal class NotificationTypeDifferConfig : DiffUtil.ItemCallback<NotificationTypeViewModel>() {
    override fun areItemsTheSame(
        oldItem: NotificationTypeViewModel,
        newItem: NotificationTypeViewModel
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: NotificationTypeViewModel,
        newItem: NotificationTypeViewModel
    ): Boolean {
        return oldItem == newItem
    }

}