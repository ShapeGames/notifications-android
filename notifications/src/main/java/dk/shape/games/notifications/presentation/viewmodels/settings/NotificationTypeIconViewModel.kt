package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage
import dk.shape.games.uikit.utils.UIDiffInterface

data class NotificationTypeIconViewModel(
    val icon: UIImage
) : UIDiffInterface {
    override val id = toString()
}

internal fun Set<SubjectNotificationType>.toIconViewModels(): List<NotificationTypeIconViewModel> =
    mapNotNull { notificationType ->
        notificationType.icon?.let { resourceIcon ->
            NotificationTypeIconViewModel(
                icon = resourceIcon.toLocalUIImage()
            )
        }
    }