package dk.shape.games.notifications.extensions

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.entities.Subscription
import java.util.*

internal fun Subscription.toActiveNotificationTypes(
    notificationGroup: SubjectNotificationGroup
): Set<SubjectNotificationType> = types.mapNotNull { subscriptionType ->
    notificationGroup.notificationTypes.find { notificationType ->
        notificationType.identifier.name.toLowerCase(Locale.ROOT) == subscriptionType
    }
}.toSet()

/*
internal fun Set<Subscription>.toSubjects(): List<Subject> = map { subscription ->
    subscription.toSubject()
}

private fun Subscription.toSubject(): Subject = Subject(
        subjectId = subjectId,
        subjectType = subjectType.toStatsSubjectType()
)

private fun SubjectType.toStatsSubjectType(): dk.shape.games.stats.favorites.SubjectType = when (this) {
    SubjectType.TEAMS -> dk.shape.games.stats.favorites.SubjectType.TEAMS
    SubjectType.ATHLETES -> dk.shape.games.stats.favorites.SubjectType.ATHLETES
    else -> throw IOException("Notifications are only supported for teams and athletes at the moment.")
}*/
