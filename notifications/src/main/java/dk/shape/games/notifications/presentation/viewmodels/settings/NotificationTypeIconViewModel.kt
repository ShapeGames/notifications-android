package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.bindings.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage

data class NotificationTypeIconViewModel(
    val icon: UIImage
) : ModuleDiffInterface {
    override fun compareContentString() = toString()

    override fun compareString() = toString()
}

internal fun Set<SubjectNotificationType>.toIconViewModels(): List<NotificationTypeIconViewModel> =
    mapNotNull { notificationType ->
        notificationType.icon?.let { resourceIcon ->
            NotificationTypeIconViewModel(
                icon = resourceIcon.toLocalUIImage()
            )
        }
    }