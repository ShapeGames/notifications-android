package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.demo.setup.eventRepositoryMock
import dk.shape.games.notifications.features.types.EventNotificationTypesConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MockEventNotificationTypesDependencyProvider : ConfigProvider<EventNotificationTypesConfig> {
    override fun config(fragment: Fragment): EventNotificationTypesConfig {
        return EventNotificationTypesConfig(
            provideDeviceId = DeviceIdProviderMock::provideDeviceIdMock,
            notificationsDataSource = NotificationsRepositoryMock,
            eventHandler = NotificationTypesEventHandlerMock,
            loadingViewProvider = loadingViewProvider,
            eventRepository = eventRepositoryMock,
            provideNotifications = NotificationsProviderMock::provideNotificationsMock
        )
    }
}
