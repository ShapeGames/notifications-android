package dk.shape.games.notifications.demo

import dk.shape.games.demoskeleton.DemoScreen
import dk.shape.games.notifications.actions.EventNotificationsAction
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.demo.notifications.EventNotificationsDependencyProvider
import dk.shape.games.notifications.demo.notifications.SubjectNotificationsDependencyProvider
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.features.list.EventNotificationsFragment
import dk.shape.games.notifications.presentation.SubjectNotificationsFragment
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
        ),
        DemoScreen(
            name = "Subject Notifications",
            fragmentProvider = {
                SubjectNotificationsFragment().apply {
                    arguments = SubjectNotificationsFragment.Args.create(
                        SubjectNotificationsAction(
                            sportId = "football:0000",
                            subjectId = "team:0000",
                            subjectName = "Manchester United",
                            subjectType = SubjectType.TEAMS
                        ),
                        SubjectNotificationsDependencyProvider::class.java
                    )
                }
            }
        )
    )
}

object HttpHeaders {
    val LOCALIZATION = "DFSDFsdf" to ""

}
