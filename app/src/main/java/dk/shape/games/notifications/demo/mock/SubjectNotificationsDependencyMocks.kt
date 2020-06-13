package dk.shape.games.notifications.demo.mock

import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.demo.notifications.SubjectNotificationsDependencyProvider
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

object SubjectNotificationsProviderMock {
    suspend fun provideNotificationsMock(): AppConfig.StatsNotifications {
        return withContext(Dispatchers.IO) {
            AppConfig.StatsNotifications(
                group = listOf(
                    AppConfig.StatsNotifications.StatsNotificationGroup(
                        sportId = "football:0000",
                        sportName = "Football",
                        defaultNotificationTypeIdentifiers = listOf(
                            AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_REMINDER,
                            AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_END
                        ),
                        notificationTypes = listOf(
                            AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                                identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_START,
                                icon = PolyIcon.Resource("ic-notifications", false),
                                name = "Startopstilling"
                            ),
                            AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                                identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_REMINDER,
                                icon = PolyIcon.Resource("ic-notifications", false),
                                name = "Inden event starter"
                            ),
                            AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                                identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.LINEUP_READY,
                                icon = PolyIcon.Resource("ic-notifications", false),
                                name = "Inden event starter"
                            ),
                            AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                                identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_END,
                                icon = PolyIcon.Resource("ic-notifications", false),
                                name = "Slutresultat for event"
                            )
                        )
                    )
                )
            )
        }
    }
}

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
        return flow {
            delay(REQUEST_DELAY)
            emit(subscriptionSetMock.sortedBy { it.eventId }.toSet())
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun hasActiveSubscription(
        deviceId: String,
        subjectId: String
    ): Flow<Boolean> {
        return getSubscriptions(deviceId).map { subscritions ->
            val subscrition = subscritions.find { it.subjectId == subjectId }
            subscrition != null && subscrition.types.isNotEmpty()
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
): DialogFragment {
    return SubjectNotificationsFragment().apply {
        arguments = SubjectNotificationsFragment.Args.create(
            action = action,
            configProvider = SubjectNotificationsDependencyProvider::class.java
        )
        show(fragment.childFragmentManager, SubjectNotificationsFragment::class.java.simpleName)
    }
}

@ExperimentalCoroutinesApi
val mockClientDependencies: MocktNotificationsConfig = MocktNotificationsConfig(
    isNotificationsSupported = SubjectNotificationsRepositoryMock::hasActiveSubscription,
    notificationEventListener = { },
    notificationsClosedListener = { },
    showNotificationsFragment = { fragment, sportId, subjectId, subjectName, subjectType ->
        launchBottomSheetNotificationsFragment(
            fragment = fragment,
            action = SubjectNotificationsAction(
                sportId = sportId,
                subjectId = subjectId,
                subjectName = subjectName,
                subjectType = subjectType
            )
        )
    }
)

@ExperimentalCoroutinesApi
object SubjectNotificationsEventHandlerMock : SubjectNotificationsEventHandler.Full {
    override fun onNotificationsActivated(subjectId: String, subjectType: SubjectType) {
        mockClientDependencies.notificationEventListener(true)
    }

    override fun onNotificationsDeactivated(subjectId: String, subjectType: SubjectType) {
        mockClientDependencies.notificationEventListener(false)
    }

    override fun <T : Parcelable> onClosed(fragment: SubjectNotificationsFragment, action: T) {
        mockClientDependencies.notificationsClosedListener(fragment)
    }
}
