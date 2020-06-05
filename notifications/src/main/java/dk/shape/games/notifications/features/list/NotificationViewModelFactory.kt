package dk.shape.games.notifications.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.usecases.NotificationUseCases

internal class NotificationViewModelFactory(
    private val notificationUseCases: NotificationUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotificationViewModel::class.java -> NotificationViewModel(
                notificationUseCases = notificationUseCases
            ) as T
            else -> throw IllegalStateException("Unsupported viewmodel")
        }
    }
}