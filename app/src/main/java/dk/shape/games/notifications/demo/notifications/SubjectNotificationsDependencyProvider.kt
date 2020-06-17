package dk.shape.games.notifications.demo.notifications

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.SubjectNotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SubjectNotificationsDependencyProvider : ConfigProvider<SubjectNotificationsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): SubjectNotificationsConfig {
        return SubjectNotificationsConfig(
            provideDeviceId = SubjectDeviceIdProviderMock::provideDeviceIdMock,
            provideNotifications = SubjectNotificationsProviderMock::provideNotificationsMock,
            notificationsDataSource = SubjectNotificationsRepositoryMock,
            eventHandler = SubjectNotificationsEventHandlerMock
        )
    }
}

class MocktNotificationsDependencyProvider : ConfigProvider<MocktNotificationsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): MocktNotificationsConfig {
        return mockClientDependencies
    }
}
