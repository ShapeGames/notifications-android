package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MockNotificationsDependencyProvider : ConfigProvider<MockSubjectNotificationsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): MockSubjectNotificationsConfig {
        return mockClientDependencies
    }
}

class MockEventParentNotificationsDependencyProvider : ConfigProvider<MockEventParentNotificationsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): MockEventParentNotificationsConfig {
        return mockEventDependencies
    }
}