package dk.shape.games.notifications.features.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.usecases.NotificationTypeUseCases

internal class NotificationTypeViewModelFactory(
    private val notificationTypeUseCases: NotificationTypeUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotificationTypeViewModel::class.java -> NotificationTypeViewModel(
                notificationTypeUseCases
            ) as T
            else -> throw IllegalStateException("Unsupported viewmodel")
        }
    }
}