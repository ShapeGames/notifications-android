package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.*
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

var savedNotificationListener: (StateDataSubject) -> Unit = {}

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
        savedNotificationListener = notificationListener
    },
    onEventNotificationTypesClicked = { fragment, legacyEventNotificationTypesAction -> }
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
                    savedNotificationListener

                override val onDismiss: () -> Unit = {
                    savedNotificationListener = {}
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
            onEventNotificationTypesClicked = { fragment, legacyEventNotificationTypesAction -> }
        )
    }
}