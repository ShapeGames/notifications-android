package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import dk.shape.games.notifications.aliases.SelectionStateNotifier
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.extensions.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage

internal class SubjectNotificationTypeViewModel(
    val icon: UIImage,
    val isDefault: Boolean,
    val identifier: StatsNotificationIdentifier,
    val stateNotifier: SelectionStateNotifier,
    val notificationName: String,
    private val initialState: Boolean
) {
    var isActivated: ObservableBoolean = ObservableBoolean(initialState)

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated.set(isChecked)
        stateNotifier(isChecked, identifier)
    }
}

internal fun StatsNotificationType.toNotificationTypeViewModel(
    selectionStateNotifier: SelectionStateNotifier = { _, _ -> },
    activatedNotifications: Set<StatsNotificationType>,
    defaultNofification: Set<StatsNotificationIdentifier>
) =
    SubjectNotificationTypeViewModel(
        icon = icon.toLocalUIImage(),
        identifier = identifier,
        isDefault = defaultNofification.contains(identifier),
        initialState = activatedNotifications.contains(this),
        stateNotifier = selectionStateNotifier,
        notificationName = name
    )