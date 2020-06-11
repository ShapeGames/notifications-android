package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.aliases.SelectionStateNotifier
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.extensions.toLocalUIImage
import dk.shape.games.uikit.databinding.UIImage

internal data class SubjectNotificationTypeViewModel(
    val icon: UIImage,
    val identifier: StatsNotificationIdentifier,
    val notificationTypeName: String,
    val isLastElement: Boolean,
    private val isDefault: Boolean,
    private val stateNotifier: SelectionStateNotifier,
    private val initialState: Boolean
): ModuleDiffInterface {
    var isActivated: ObservableBoolean = ObservableBoolean(initialState)

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated.set(isChecked)
        stateNotifier(isChecked, identifier)
    }

    override fun compareContentString() = toString()
    override fun compareString() = identifier.name
}

internal fun StatsNotificationType.toNotificationTypeViewModel(
    isLastElement: Boolean,
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
        isLastElement = isLastElement,
        notificationTypeName = name
    )