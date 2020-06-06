package dk.shape.games.notifications.demo

import dk.shape.games.demoskeleton.DemoScreen
import dk.shape.games.notifications.actions.NotificationsAction
import dk.shape.games.notifications.demo.notifications.NotificationsDependencyProvider
import dk.shape.games.notifications.features.list.NotificationsFragment
import kotlin.time.ExperimentalTime

object NotificationsScreens {

    @ExperimentalTime
    val screens = listOf(
        DemoScreen(
            name = "Legacy Notifications",
            fragmentProvider = {
                NotificationsFragment().apply {
                    arguments = NotificationsFragment.Args.create(
                        NotificationsAction(true),
                        NotificationsDependencyProvider::class.java
                    )
                }
            }
        )
    )
}
