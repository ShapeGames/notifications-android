package dk.shape.games.notifications.usecases

import androidx.annotation.MainThread
import dk.shape.games.notifications.aliases.LegacyNotificationGroup
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription

interface LegacyEventNotificationsUseCases {

    suspend fun loadAllSubscriptions(
        eventIds: List<String>?,
        appConfig: AppConfig,
        onSaveEventIds: (List<String>) -> Unit
    ): List<LoadedLegacySubscription>

    @MainThread
    fun updateNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    )
}

data class LoadedLegacySubscription(
    val event: Event,
    val subscription: Subscription,
    val notificationGroup: LegacyNotificationGroup
)