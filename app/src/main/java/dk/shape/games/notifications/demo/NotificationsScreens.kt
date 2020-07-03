package dk.shape.games.notifications.demo

import dk.shape.games.demoskeleton.DemoScreen
import dk.shape.games.notifications.actions.EventNotificationsAction
import dk.shape.games.notifications.actions.NotificationSettingsAction
import dk.shape.games.notifications.demo.mock.SubjectNotificationsAction
import dk.shape.games.notifications.demo.mock.MockSubjectNotificationsParentFragment
import dk.shape.games.notifications.demo.dependency.MockEventNotificationsDependencyProvider
import dk.shape.games.notifications.demo.dependency.MockNotificationSettingsDependencyProvider
import dk.shape.games.notifications.demo.dependency.MockNotificationSettingsEmptyDependencyProvider
import dk.shape.games.notifications.demo.dependency.MockNotificationsDependencyProvider
import dk.shape.games.notifications.features.list.EventNotificationsFragment
import dk.shape.games.notifications.presentation.NotificationSettingsFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

object NotificationsScreens {

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    val screens = listOf(
        DemoScreen(
            name = "Event Notifications",
            fragmentProvider = {
                EventNotificationsFragment().apply {
                    arguments = EventNotificationsFragment.Args.create(
                        EventNotificationsAction(true),
                        MockEventNotificationsDependencyProvider::class.java
                    )
                }
            }
        ),
        DemoScreen(
            name = "Bottom Sheet Notifications",
            fragmentProvider = {
                MockSubjectNotificationsParentFragment().apply {
                    arguments = MockSubjectNotificationsParentFragment.Args.create(
                        SubjectNotificationsAction,
                        MockNotificationsDependencyProvider::class.java
                    )
                }
            }
        ),
        DemoScreen(
            name = "Notifications Settings",
            fragmentProvider = {
                NotificationSettingsFragment().apply {
                    arguments = NotificationSettingsFragment.Args.create(
                        NotificationSettingsAction(),
                        MockNotificationSettingsDependencyProvider::class.java
                    )
                }
            }
        ),
        DemoScreen(
            name = "Notifications Settings Empty",
            fragmentProvider = {
                NotificationSettingsFragment().apply {
                    arguments = NotificationSettingsFragment.Args.create(
                        NotificationSettingsAction(),
                        MockNotificationSettingsEmptyDependencyProvider::class.java
                    )
                }
            }
        )
    )
}