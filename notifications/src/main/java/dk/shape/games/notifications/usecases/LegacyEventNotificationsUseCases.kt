package dk.shape.games.notifications.usecases

import androidx.annotation.MainThread
import dk.shape.games.notifications.aliases.EventNotificationsLoadedListener
import dk.shape.games.notifications.aliases.LegacyNotificationGroup
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription

interface LegacyEventNotificationsUseCases {

    suspend fun loadSubscription(
        eventId: String,
        notificationGroupId: String,
        provideNotifications: suspend () -> List<LegacyNotificationGroup>,
        onSuccess: EventNotificationsLoadedListener,
        onError: () -> Unit
    )

    suspend fun loadAllSubscriptions(
        eventIds: List<String>?,
        appConfig: AppConfig,
        onSaveEventIds: (List<String>) -> Unit,
        provideEvents: suspend (eventIds: List<String>) -> List<Event>
    ): List<LoadedLegacySubscription>

    suspend fun loadAllNotifications(
        eventIds: List<String>?,
        appConfig: AppConfig,
        onSaveEventIds: (List<String>) -> Unit,
        provideEvents: suspend (eventIds: List<String>) -> List<Event>
    ): List<LoadedLegacySubscription>

    @MainThread
    fun updateNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )
}

data class LoadedLegacySubscription(
    val event: Event,
    val subscription: Subscription,
    val notificationGroup: LegacyNotificationGroup
)