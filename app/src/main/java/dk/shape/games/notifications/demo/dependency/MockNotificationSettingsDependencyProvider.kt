package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.*
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataEvent
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class MockNotificationSettingsDependencyProvider : ConfigProvider<NotificationSettingsConfig> {
    override fun config(fragment: Fragment): NotificationSettingsConfig =
        mockNotificationSettingsConfig
}

var savedSubjectNotificationListener: (StateDataSubject) -> Unit = {}
var savedEventNotificationListener: (StateDataEvent) -> Unit = {}

@ExperimentalCoroutinesApi
@ExperimentalTime
private val mockNotificationSettingsConfig = NotificationSettingsConfig(
    legacyNotificationsComponent = mockLegacyNotificationsComponent,
    subjectNotificationsDataSource = mockSubjectNotificationsDataSource,
    provideEventIdsForUserBetsAsync = {},
    provideEventIdsForBetSlip = { null },
    provideSubjectInfo = {
        mockSubjectInfos
    },
    provideAppConfig = {
        mockAppConfig
    },
    provideEvents = {
        mockEvents
    },
    provideDeviceId = {
        delay(1500)
        "device:1234"
    },
    onBackPressed = {},
    onSubjectNotificationTypesClicked = { fragment, subjectNotificationTypesAction, notificationListener ->
        NotificationSettingsSubjectFragment().apply {
            arguments = NotificationSettingsSubjectFragment.Args.create(
                subjectNotificationTypesAction,
                MockNotificationSettingsSubjectDependencyProvider::class.java
            )
            show(
                fragment.childFragmentManager,
                NotificationSettingsSubjectFragment::class.java.simpleName
            )
        }
        savedSubjectNotificationListener = notificationListener
    },
    onEventNotificationTypesClicked = { fragment, legacyEventNotificationTypesAction, notificationListener ->
        NotificationSettingsEventFragment().apply {
            arguments = NotificationSettingsEventFragment.Args.create(
                legacyEventNotificationTypesAction,
                MockNotificationSettingsEventDependencyProvider::class.java
            )
            show(
                fragment.childFragmentManager,
                NotificationSettingsEventFragment::class.java.simpleName
            )
        }
        savedEventNotificationListener = notificationListener
    }
)

@ExperimentalTime
class MockNotificationSettingsSubjectDependencyProvider :
    ConfigProvider<NotificationSettingsSubjectConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): NotificationSettingsSubjectConfig {
        return NotificationSettingsSubjectConfig(
            notificationsDataSource = mockSubjectNotificationsDataSource,
            provideDeviceId = {
                "device:1234"
            },
            eventListener = object : NotificationSettingsSubjectEventListener {
                override val onNotificationTypesChanged: (StateDataSubject) -> Unit =
                    savedSubjectNotificationListener

                override val onDismiss: () -> Unit = {
                    savedSubjectNotificationListener = {}
                }
            }
        )
    }
}

@ExperimentalTime
class MockNotificationSettingsEventDependencyProvider :
    ConfigProvider<NotificationSettingsEventConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): NotificationSettingsEventConfig {
        return NotificationSettingsEventConfig(
            notificationsDataSource = mockLegacyNotificationsComponent,
            provideDeviceId = {
                "device:1234"
            },
            eventListener = object : NotificationSettingsEventEventListener {
                override val onNotificationTypesChanged: (StateDataEvent) -> Unit =
                    savedEventNotificationListener

                override val onDismiss: () -> Unit = {
                    savedEventNotificationListener = {}
                }
            }
        )
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
class MockNotificationSettingsEmptyDependencyProvider : ConfigProvider<NotificationSettingsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): NotificationSettingsConfig {
        return NotificationSettingsConfig(
            legacyNotificationsComponent = mockLegacyNotificationsComponent,
            subjectNotificationsDataSource = mockSubjectNotificationsDataSource,
            provideEventIdsForUserBetsAsync = {},
            provideEventIdsForBetSlip = { null },
            provideSubjectInfo = {
                listOf()
            },
            provideAppConfig = {
                mockAppConfig
            },
            provideEvents = {
                listOf()
            },
            provideDeviceId = {
                "device:1234"
            },
            onBackPressed = {},
            onSubjectNotificationTypesClicked = { fragment, subjectNotificationTypesAction, listener -> },
            onEventNotificationTypesClicked = { fragment, legacyEventNotificationTypesAction, listener -> }
        )
    }
}