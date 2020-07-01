package dk.shape.games.notifications.presentation.viewmodels.settings

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.actions.LegacyEventNotificationTypesAction
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription
import dk.shape.games.notifications.extensions.toEventInfo
import dk.shape.games.notifications.extensions.toTeamNamesPair

data class NotificationsSettingsEventViewModel(
    private val event: Event,
    private val subscription: Subscription,
    private val notificationGroup: AppConfig.Notifications.NotificationGroup,
    private val onEventNotificationTypesClicked: (LegacyEventNotificationTypesAction) -> Unit,
    private val onSetNotifications: (notificationIds: Set<String>, onError: () -> Unit) -> Unit
) {
    private val eventId = event.id

    private val initialActiveNotificationIds: Set<String> = subscription.toTypeIds()
    private val eventName: Pair<String, String?> = event.toTeamNamesPair()

    val homeTeam: String = eventName.first
    val awayTeam: String? = eventName.second

    val infoTextViewModel: ObservableField<NotificationInfoViewModel.Text> =
        ObservableField(
            initialActiveNotificationIds.toFormattedString().toNotificationInfoViewModel()
        )

    private var activeNotificationIds: Set<String> = initialActiveNotificationIds
        set(value) {
            if (field != value) {
                infoTextViewModel.set(value.toFormattedString().toNotificationInfoViewModel())
                isSwitchToggled.set(value.isNotEmpty())
                field = value
            }
        }

    val isSwitchToggled: ObservableBoolean =
        ObservableBoolean(initialActiveNotificationIds.isNotEmpty())

    val onSwitchStateChanged = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        if (isSwitchToggled.get() == isChecked) {
            return@OnCheckedChangeListener
        }
        toggleNotifications(isChecked)
    }

    val onSettingsClicked = View.OnClickListener {
        onEventNotificationTypesClicked(
            LegacyEventNotificationTypesAction(
                eventId = eventId,
                eventInfo = event.toEventInfo(),
                possibleNotifications = notificationGroup.notificationTypes,
                initialActiveNotificationIds = activeNotificationIds
            )
        )
    }

    private fun toggleNotifications(isChecked: Boolean) {
        setNotificationsAsync(
            notificationTypeIds = if (isChecked) {
                notificationGroup.defaultNotificationTypeIdentifiers.toSet()
            } else emptySet()
        )
    }

    private fun setNotificationsAsync(
        notificationTypeIds: Set<String>
    ) {
        val previousNotificationIds = activeNotificationIds
        activeNotificationIds = notificationTypeIds

        onSetNotifications(notificationTypeIds) {
            activeNotificationIds = previousNotificationIds
        }

    }

    private fun Set<String>.toFormattedString(): String = mapNotNull { typeId ->
        notificationGroup.notificationTypes.find { notificationType ->
            notificationType.identifier == typeId
        }?.name
    }.joinToString()

    private fun Subscription.toTypeIds(): Set<String> = commaSeparatedTypes.split(',').toSet()
}