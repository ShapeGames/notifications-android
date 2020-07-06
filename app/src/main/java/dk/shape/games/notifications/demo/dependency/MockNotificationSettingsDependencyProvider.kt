package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.NotificationSettingsConfig
import dk.shape.games.notifications.presentation.SubjectInfo
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MockNotificationSettingsDependencyProvider : ConfigProvider<NotificationSettingsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): NotificationSettingsConfig {
        return NotificationSettingsConfig(
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
                "device:1234"
            },
            onBackPressed = {},
            onSubjectNotificationTypesClicked = { fragment, subjectNotificationTypesAction -> },
            onEventNotificationTypesClicked = { fragment, legacyEventNotificationTypesAction -> }
        )
    }
}

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
            onSubjectNotificationTypesClicked = { fragment, subjectNotificationTypesAction -> },
            onEventNotificationTypesClicked = { fragment, legacyEventNotificationTypesAction -> }
        )
    }
}