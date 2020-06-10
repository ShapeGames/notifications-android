package dk.shape.games.notifications.demo

import dk.shape.games.demoskeleton.DemoScreen
import dk.shape.games.notifications.actions.EventNotificationsAction
import dk.shape.games.notifications.demo.notifications.EventNotificationsDependencyProvider
import dk.shape.games.notifications.features.list.EventNotificationsFragment
import kotlin.time.ExperimentalTime

object NotificationsScreens {

    @ExperimentalTime
    val screens = listOf(
        DemoScreen(
            name = "Event Notifications",
            fragmentProvider = {
                EventNotificationsFragment().apply {
                    arguments = EventNotificationsFragment.Args.create(
                        EventNotificationsAction(true),
                        EventNotificationsDependencyProvider::class.java
                    )
                }
            }
        )
    )
}
