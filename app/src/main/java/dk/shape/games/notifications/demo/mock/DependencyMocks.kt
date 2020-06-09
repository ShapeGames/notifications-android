package dk.shape.games.notifications.demo.mock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import dk.shape.games.notifications.actions.NotificationTypesAction
import dk.shape.games.notifications.demo.R
import dk.shape.games.notifications.demo.notifications.NotificationTypesDependencyProvider
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.features.list.NotificationsEventHandler
import dk.shape.games.notifications.features.list.EventNotificationsFragment
import dk.shape.games.notifications.features.types.NotificationTypesEventHandler
import dk.shape.games.notifications.features.types.NotificationTypesFragment
import dk.shape.games.notifications.repositories.NotificationsDataSource
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.ExperimentalTime

object DeviceIdProviderMock {
    suspend fun provideDeviceIdMock(): String {
        return "1db655ee-48ea-11ea-b77f-2e728ce88125"
    }
}

object NotificationsProviderMock {
    suspend fun provideNotificationsMock(): AppConfig.Notifications {
        return AppConfig.Notifications(
            group = listOf(
                AppConfig.Notifications.NotificationGroup(
                    groupId = "21",
                    defaultNotificationTypeIdentifiers = listOf(
                        "team_home_score",
                        "team_away_score"
                    ),
                    notificationTypes = listOf(
                        AppConfig.Notifications.NotificationGroup.NotificationType(
                            "match_start", "Match start"
                        ),
                        AppConfig.Notifications.NotificationGroup.NotificationType(
                            "match_end", "Match end"
                        ),
                        AppConfig.Notifications.NotificationGroup.NotificationType(
                            "team_home_score", "Home team scored"
                        ),
                        AppConfig.Notifications.NotificationGroup.NotificationType(
                            "team_away_score", "Away team scored"
                        )
                    )
                )
            )
        )
    }
}

object NotificationsRepositoryMock : NotificationsDataSource {

    private val subscriptionSetMock = mutableSetOf(
        Subscription.Events(
            eventId = "event1",
            types = setOf("match_start", "match_end")
        ),
        Subscription.Events(
            eventId = "event2",
            types = setOf("team_home_score", "team_away_score")
        )
    )

    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> {
        return flow {
            emit(
                subscriptionSetMock.sortedBy {
                    it.eventId
                }.toSet()
            )
        }
    }

    override suspend fun updateSubscriptions(
        deviceId: String,
        eventId: String,
        subscribedNotificationTypeIds: Set<String>
    ) {
        subscriptionSetMock.find { it.eventId == eventId }?.let { subscriptionSetMock.remove(it) }
        subscriptionSetMock.add(
            Subscription.Events(
                eventId = eventId,
                types = subscribedNotificationTypeIds.toSet()
            )
        )
    }

    override suspend fun register(
        deviceId: String,
        platform: String,
        environment: String,
        notificationToken: String
    ) {

    }

    override fun isUpdatingSubscriptions(deviceId: String): Boolean {
        return false
    }

}

val loadingViewProvider: (context: Context) -> View = { context ->
    LayoutInflater.from(context).inflate(R.layout.view_loading, null, false)
}

object notificationsEventHandlerMock : NotificationsEventHandler {
    override fun onBackPress(notificationsFragment: EventNotificationsFragment) {
        notificationsFragment.parentFragmentManager.popBackStack()
    }

    @ExperimentalTime
    override fun onConfigurationClick(
        notificationsFragment: EventNotificationsFragment,
        forEventId: String
    ) {
        NotificationTypesFragment().apply {
            arguments = NotificationTypesFragment.Args.create(
                NotificationTypesAction(forEventId),
                NotificationTypesDependencyProvider::class.java
            )
        }.also {
            notificationsFragment.parentFragmentManager.beginTransaction().add(
                R.id.fragmentContainer, it
            ).commit()
        }
    }

    override fun onToggleError(e: Throwable) {

    }
}

object BetIdsProviderMock {
    suspend fun provideBetIdsMock(): List<String> {
        return listOf("event3")
    }
}

object NotificationTypesEventHandlerMock : NotificationTypesEventHandler {
    override fun onBackPress(notificationTypesFragment: NotificationTypesFragment) {
        notificationTypesFragment.parentFragmentManager.popBackStack()
    }

    override fun onToggleError(e: Throwable) {

    }
}
