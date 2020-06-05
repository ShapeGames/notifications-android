package dk.shape.games.notifications.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.aliases.ViewProvider
import dk.shape.games.notifications.usecases.NotificationsUseCases
import dk.shape.games.sportsbook.offerings.modules.event.data.Event

internal class NotificationsViewModelFactory(
    private val notificationsUseCases: NotificationsUseCases,
    private val provideNotificationViewModel: (event: Event) -> NotificationViewModel,
    private val loadingViewProvider: ViewProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotificationsViewModel::class.java -> NotificationsViewModel(
                notificationsUseCases,
                loadingViewProvider,
                provideNotificationViewModel
            ) as T
            else -> throw IllegalStateException("Unsupported viewmodel")
        }
    }
}