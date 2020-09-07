package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.EventNotificationsSheetConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MockEventNotificationsSheetDependencyProvider : ConfigProvider<EventNotificationsSheetConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment) = EventNotificationsSheetConfig(
        provideNotifications = EventNotificationsProviderMock::provideNotificationsMock,
        provideNotificationsNow = { mockEventNotificationGroups },
        notificationsDataSource = mockLegacyNotificationsComponent,
        eventHandler = EventNotificationsSheetEventHandlerMock
    )
}

