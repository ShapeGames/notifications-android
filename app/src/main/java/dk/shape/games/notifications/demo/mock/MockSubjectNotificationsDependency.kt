package dk.shape.games.notifications.demo.mock

import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.demo.dependency.MockSubjectNotificationsDependencyProvider
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.presentation.SubjectNotificationsEventHandler
import dk.shape.games.notifications.presentation.SubjectNotificationsFragment
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val REQUEST_DELAY = 1000L

object SubjectDeviceIdProviderMock {
    suspend fun provideDeviceIdMock(): String {
        return withContext(Dispatchers.IO) {
            "1db655ee-48ea-11ea-b77f-2e728ce88125"
        }
    }
}

object SportNotificationsSupportMock {
    suspend fun hasNotificationsSupport(sportId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val notifications = SubjectNotificationsProviderMock.provideNotificationsMock()
            notifications.any { it.sportId == sportId }
        }
    }
}

object SubjectNotificationsProviderMock {
    suspend fun provideNotificationsMock(): List<AppConfig.SubjectNotificationGroup> {
        return withContext(Dispatchers.IO) {
            subjectNotifications
        }
    }
}

val subjectNotifications = listOf(
    AppConfig.SubjectNotificationGroup(
        sportId = "football:0000",
        sportName = "Football",
        defaultNotificationTypeIdentifiers = listOf(
            AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_REMINDER,
            AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_END
        ),
        notificationTypes = listOf(
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.LINEUP_READY,
                icon = PolyIcon.Resource("icon-lineup-ready", false),
                name = "Startopstilling"
            ),
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_REMINDER,
                icon = PolyIcon.Resource("icon-event-reminder", false),
                name = "Inden event starter"
            ),
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_START,
                icon = PolyIcon.Resource("icon-event-start", false),
                name = "Inden event starter"
            ),
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_END,
                icon = PolyIcon.Resource("icon-event-end", false),
                name = "Slutresultat for event"
            )
        )
    )
)

object SubjectNotificationsRepositoryMock : SubjectNotificationsDataSource {

    private val subscriptionSetMock = mutableSetOf(
        Subscription(
            eventId = "team:0000",
            subjectId = "team:0000",
            subjectType = SubjectType.TEAMS,
            types = setOf()
        ),
        Subscription(
            eventId = "team:0001",
            subjectId = "team:0001",
            subjectType = SubjectType.TEAMS,
            types = setOf("event_start", "event_end")
        )
    )

    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> {
        return getAllSubscriptions(deviceId)
    }

    override suspend fun getAllSubscriptions(deviceId: String): Flow<Set<Subscription>> {
        return flow {
            delay(REQUEST_DELAY)
            emit(subscriptionSetMock.sortedBy { it.eventId }.toSet())
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun hasActiveSubjectSubscription(
        deviceId: String,
        subjectId: String
    ): Flow<Boolean> {
        return getSubscriptions(deviceId).map { subscriptions ->
            val subscription = subscriptions.find { it.subjectId == subjectId }
            subscription != null && subscription.types.isNotEmpty()
        }
    }

    override suspend fun updateSubjectSubscriptions(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    ) {
        delay(REQUEST_DELAY)

        subscriptionSetMock.find { it.subjectId == subjectId }
            ?.let { subscriptionSetMock.remove(it) }
        subscriptionSetMock.add(
            Subscription(
                eventId = subjectId,
                subjectId = subjectId,
                subjectType = subjectType,
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
        delay(REQUEST_DELAY)
    }
}

fun <T : Fragment> launchBottomSheetNotificationsFragment(
    fragment: T,
    action: SubjectNotificationsAction
): BottomSheetDialogFragment =
    SubjectNotificationsFragment().apply {
        arguments = SubjectNotificationsFragment.Args.create(
            action = action,
            configProvider = MockSubjectNotificationsDependencyProvider::class.java
        )
        show(fragment.childFragmentManager, SubjectNotificationsFragment::class.java.simpleName)
    }

@ExperimentalCoroutinesApi
val mockClientDependencies: MockSubjectNotificationsConfig = MockSubjectNotificationsConfig(
    hasSportNotificationsSupport = { sportId ->
        SportNotificationsSupportMock.hasNotificationsSupport(sportId)
    },
    hasNotificationsSupport = {
        val deviceId = SubjectDeviceIdProviderMock.provideDeviceIdMock()
        SubjectNotificationsRepositoryMock.hasActiveSubjectSubscription(deviceId, it)
    },
    notificationsEventListener = { },
    showNotificationsFragment = { fragment, mockData ->
        launchBottomSheetNotificationsFragment(
            fragment = fragment,
            action = SubjectNotificationsAction(
                sportId = mockData.sportId,
                subjectId = mockData.subjectId,
                subjectName = mockData.subjectName,
                subjectType = mockData.subjectType,
                shouldShowHeaderTitle = false
            )
        )
    }
)

@ExperimentalCoroutinesApi
object SubjectNotificationsEventHandlerMock : SubjectNotificationsEventHandler.Full {
    override fun onNotificationsActivated(subjectId: String, subjectType: SubjectType) {
        mockClientDependencies.notificationsEventListener(true)
    }

    override fun onNotificationsDeactivated(subjectId: String, subjectType: SubjectType) {
        mockClientDependencies.notificationsEventListener(false)
    }

    override fun onDismissed() {}
}
