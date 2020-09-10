package dk.shape.games.notifications.demo.mock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.actions.EventNotificationTypesAction
import dk.shape.games.notifications.actions.EventNotificationsSheetAction
import dk.shape.games.notifications.demo.R
import dk.shape.games.notifications.demo.dependency.MockEventNotificationTypesDependencyProvider
import dk.shape.games.notifications.demo.dependency.MockEventNotificationsSheetDependencyProvider
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.features.list.NotificationsEventHandler
import dk.shape.games.notifications.features.list.EventNotificationsFragment
import dk.shape.games.notifications.features.types.NotificationTypesEventHandler
import dk.shape.games.notifications.features.types.EventNotificationTypesFragment
import dk.shape.games.notifications.presentation.*
import dk.shape.games.notifications.repositories.EventNotificationsDataSource
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.notifications.entities.SubjectType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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

object NotificationsRepositoryMock : EventNotificationsDataSource {

    private val subscriptionSetMock = mutableSetOf<Subscription>(
        Subscription(
            eventId = "event1",
            subjectId = "event1",
            subjectType = SubjectType.EVENTS,
            types = setOf("match_start", "match_end")
        ),
        Subscription(
            eventId = "event2",
            subjectId = "event2",
            subjectType = SubjectType.EVENTS,
            types = setOf("team_home_score", "team_away_score")
        ),
        Subscription(
            eventId = "event:1234",
            subjectId = "event:1234",
            subjectType = SubjectType.EVENTS,
            types = setOf("red_card", "yellow_card")
        )
    )

    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> {
        return getAllSubscriptions(deviceId)
    }

    override suspend fun getAllSubscriptions(deviceId: String): Flow<Set<Subscription>> {
        return flow {
            emit(
                subscriptionSetMock.sortedBy {
                    it.eventId
                }.toSet()
            )
        }
    }

    override suspend fun updateEventSubscriptions(
        deviceId: String,
        eventId: String,
        subscribedNotificationTypeIds: Set<String>
    ) {
        subscriptionSetMock.find { it.eventId == eventId }?.let { subscriptionSetMock.remove(it) }
        subscriptionSetMock.add(
            Subscription(
                eventId = eventId,
                subjectId = eventId,
                subjectType = SubjectType.EVENTS,
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
        EventNotificationTypesFragment().apply {
            arguments = EventNotificationTypesFragment.Args.create(
                EventNotificationTypesAction(forEventId),
                MockEventNotificationTypesDependencyProvider::class.java
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
    override fun onBackPress(notificationTypesFragment: EventNotificationTypesFragment) {
        notificationTypesFragment.parentFragmentManager.popBackStack()
    }

    override fun onToggleError(e: Throwable) {

    }
}

object GroupNotificationsSupportMock {
    suspend fun hasNotificationsSupport(eventId: String): Boolean {
        return true
    }
}

object EventNotificationsProviderMock {
    suspend fun provideNotificationsMock(): List<AppConfig.Notifications.NotificationGroup> {
        return withContext(Dispatchers.IO) {
            mockEventNotificationGroups
        }
    }
}

@ExperimentalCoroutinesApi
val mockEventDependencies: MockEventParentNotificationsConfig = MockEventParentNotificationsConfig(
    hasGroupNotificationsSupport = { groupId ->
        GroupNotificationsSupportMock.hasNotificationsSupport(groupId)
    },
    hasNotificationsSupport = { eventId ->
        flow {
            delay(1000)
            emit(true)
        }
    },
    notificationsEventListener = { },
    showNotificationsFragment = { fragment, mockData ->
        launchEventBottomSheetNotificationsFragment(
            fragment = fragment,
            action = EventNotificationsSheetAction(
                groupId = mockData.groupId,
                eventId = mockData.eventId,
                eventInfo = mockData.eventInfo
            )
        )
    }
)

private fun <T : Fragment> launchEventBottomSheetNotificationsFragment(
    fragment: T,
    action: EventNotificationsSheetAction
): BottomSheetDialogFragment =
    EventNotificationsSheetFragment().apply {
        arguments = EventNotificationsSheetFragment.Args.create(
            action = action,
            configProvider = MockEventNotificationsSheetDependencyProvider::class.java
        )
        show(fragment.childFragmentManager, EventNotificationsSheetFragment::class.java.simpleName)
    }

@ExperimentalCoroutinesApi
object EventNotificationsSheetEventHandlerMock : EventNotificationsSheetEventHandler {
    override fun onSubscriptionsUpdated(eventId: String, hasActiveSubscriptions: Boolean) {
        mockEventDependencies.notificationsEventListener(hasActiveSubscriptions)
    }

    override fun onDismissed() {}
}

