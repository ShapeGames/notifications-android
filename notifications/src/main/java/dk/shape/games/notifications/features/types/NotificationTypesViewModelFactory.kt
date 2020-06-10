package dk.shape.games.notifications.features.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.usecases.EventNotificationTypesUseCases

internal class NotificationTypesViewModelFactory(
    private val notificationTypesUseCases: EventNotificationTypesUseCases,
    private val loadingViewProvider: ViewProvider,
    private val createNotificationTypeViewModel: (eventId: String, notificationTypeId: String) -> NotificationTypeViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            EventNotificationTypesViewModel::class.java -> EventNotificationTypesViewModel(
                notificationTypesUseCases,
                loadingViewProvider,
                createNotificationTypeViewModel
            ) as T
            else -> throw IllegalStateException("Unsupported viewmodel")
        }
    }
}