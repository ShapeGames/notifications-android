package dk.shape.games.notifications.demo

import dk.shape.games.demoskeleton.DemoScreen
import dk.shape.games.notifications.actions.EventNotificationsAction
import dk.shape.games.notifications.actions.NotificationSettingsAction
import dk.shape.games.notifications.demo.dependency.*
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.features.list.EventNotificationsFragment
import dk.shape.games.notifications.presentation.NotificationSettingsEventFragment
import dk.shape.games.notifications.presentation.NotificationSettingsFragment
import dk.shape.games.notifications.presentation.NotificationSettingsSubjectFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.time.ExperimentalTime

object NotificationsScreens {

    @FlowPreview
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
            name = "Notifications Sheet Subject",
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
            name = "Notifications Sheet Event",
            fragmentProvider = {
                MockEventNotificationsParentFragment().apply {
                    arguments = MockEventNotificationsParentFragment.Args.create(
                        EventParentNotificationsAction,
                        MockEventParentNotificationsDependencyProvider::class.java
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
            name = "Notifications Settings (empty state)",
            fragmentProvider = {
                NotificationSettingsFragment().apply {
                    arguments = NotificationSettingsFragment.Args.create(
                        NotificationSettingsAction(),
                        MockNotificationSettingsEmptyDependencyProvider::class.java
                    )
                }
            }
        ),
        DemoScreen(
            name = "Notification Settings Subject",
            fragmentProvider = {
                NotificationSettingsSubjectFragment().apply {
                    arguments = NotificationSettingsSubjectFragment.Args.create(
                        mockNotificationSettingsSubjectAction,
                        MockNotificationSettingsSubjectDependencyProvider::class.java
                    )
                }
            }
        ),
        DemoScreen(
            name = "Notification Settings Event",
            fragmentProvider = {
                NotificationSettingsEventFragment().apply {
                    arguments = NotificationSettingsEventFragment.Args.create(
                        mockNotificationSettingsEventAction,
                        MockNotificationSettingsEventDependencyProvider::class.java
                    )
                }
            }
        )
    )
}