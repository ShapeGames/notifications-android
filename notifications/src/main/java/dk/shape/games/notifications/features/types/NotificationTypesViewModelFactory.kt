package dk.shape.games.notifications.features.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.usecases.NotificationTypesUseCases

internal class NotificationTypesViewModelFactory(
    private val notificationTypesUseCases: NotificationTypesUseCases,
    private val loadingViewProvider: ViewProvider,
    private val createNotificationTypeViewModel: (eventId: String, notificationTypeId: String) -> NotificationTypeViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotificationTypesViewModel::class.java -> NotificationTypesViewModel(
                notificationTypesUseCases,
                loadingViewProvider,
                createNotificationTypeViewModel
            ) as T
            else -> throw IllegalStateException("Unsupported viewmodel")
        }
    }
}