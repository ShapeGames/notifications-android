package dk.shape.games.notifications.presentation.viewmodels.settings

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.actions.NotificationSettingsEventAction
import dk.shape.games.notifications.aliases.LegacyNotificationGroup
import dk.shape.games.notifications.extensions.toDefaultOrAllNotifications
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription
import dk.shape.games.notifications.extensions.toEventInfo
import dk.shape.games.notifications.extensions.toTeamNamesPair
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataEvent
import dk.shape.games.notifications.usecases.LoadedLegacySubscription
import dk.shape.games.notifications.usecases.LoadedNotifications

typealias OnEventNotificationTypesClicked = (NotificationSettingsEventAction) -> Unit
typealias OnSetEventNotifications = (notificationIds: Set<String>, onError: () -> Unit) -> Unit

open class NotificationsSettingsEventViewModel(
    private val event: Event,
    open val subscription: Subscription,
    private val notificationGroup: LegacyNotificationGroup,
    private val onEventNotificationTypesClicked: OnEventNotificationTypesClicked,
    private val onSetNotifications: OnSetEventNotifications
) {
    private val eventId = event.id

    private val initialActiveNotificationIds: Set<String> = subscription.types.toSet()
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
            NotificationSettingsEventAction(
                eventId = eventId,
                eventInfo = event.toEventInfo(),
                possibleNotifications = notificationGroup.notificationTypes,
                initialActiveNotificationIds = activeNotificationIds,
                defaultNotificationIds = notificationGroup.defaultNotificationTypeIdentifiers.toSet()
            )
        )
    }

    private fun toggleNotifications(isChecked: Boolean) {
        setNotificationsAsync(
            notificationTypeIds = if (isChecked) {
                notificationGroup.toDefaultOrAllNotifications()
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

    fun update(stateData: StateDataEvent) {
        activeNotificationIds = stateData.notificationTypeIds
    }

    private fun Set<String>.toFormattedString(): String = mapNotNull { typeId ->
        notificationGroup.notificationTypes.find { notificationType ->
            notificationType.identifier == typeId
        }?.name
    }.joinToString()

    class Subscribed(
        event: Event,
        override val subscription: Subscription,
        notificationGroup: LegacyNotificationGroup,
        onEventNotificationTypesClicked: OnEventNotificationTypesClicked,
        onSetNotifications: OnSetEventNotifications
    ): NotificationsSettingsEventViewModel(
        event, subscription, notificationGroup, onEventNotificationTypesClicked, onSetNotifications
    )

    class Unsubscribed(
        event: Event,
        override val subscription: Subscription,
        notificationGroup: LegacyNotificationGroup,
        onEventNotificationTypesClicked: OnEventNotificationTypesClicked,
        onSetNotifications: OnSetEventNotifications
    ): NotificationsSettingsEventViewModel(
        event, subscription, notificationGroup, onEventNotificationTypesClicked, onSetNotifications
    )
}

internal fun LoadedNotifications.toNotificationsSettingsEventViewModel(
    onEventNotificationTypesClicked: OnEventNotificationTypesClicked,
    onSetNotifications: OnSetEventNotifications
) = NotificationsSettingsEventViewModel.Unsubscribed(
    event = event,
    subscription = subscription,
    notificationGroup = notificationGroup,
    onEventNotificationTypesClicked = onEventNotificationTypesClicked,
    onSetNotifications = onSetNotifications
)

internal fun LoadedLegacySubscription.toNotificationsSettingsEventViewModel(
    onEventNotificationTypesClicked: OnEventNotificationTypesClicked,
    onSetNotifications: OnSetEventNotifications
) = NotificationsSettingsEventViewModel.Subscribed(
    event = event,
    subscription = subscription,
    notificationGroup = notificationGroup,
    onEventNotificationTypesClicked = onEventNotificationTypesClicked,
    onSetNotifications = onSetNotifications
)