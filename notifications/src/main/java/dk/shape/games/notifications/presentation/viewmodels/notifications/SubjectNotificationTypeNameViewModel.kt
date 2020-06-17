package dk.shape.games.notifications.presentation.viewmodels.notifications

internal sealed class SubjectNotificationTypeNameViewModel(open val name: String) {
    data class Active(override val name: String): SubjectNotificationTypeNameViewModel(name)
    data class Normal(override val name: String): SubjectNotificationTypeNameViewModel(name)
}