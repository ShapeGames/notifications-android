package dk.shape.games.notifications.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.usecases.EventNotificationUseCases

internal class NotificationViewModelFactory(
    private val notificationUseCases: EventNotificationUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            EventNotificationViewModel::class.java -> EventNotificationViewModel(
                notificationUseCases = notificationUseCases
            ) as T
            else -> throw IllegalStateException("Unsupported viewmodel")
        }
    }
}