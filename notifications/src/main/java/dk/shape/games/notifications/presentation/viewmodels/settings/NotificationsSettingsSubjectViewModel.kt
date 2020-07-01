package dk.shape.games.notifications.presentation.viewmodels.settings

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.actions.SubjectNotificationTypesAction
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.extensions.toActiveNotificationTypes
import dk.shape.games.notifications.extensions.toDefaultNotificationTypes
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import kotlin.time.ExperimentalTime

data class NotificationsSettingsSubjectViewModel(
    val name: String,
    private val subscription: Subscription,
    private val notificationGroup: AppConfig.SubjectNotificationGroup,
    private val onSubjectNotificationTypesClicked: (SubjectNotificationTypesAction) -> Unit,
    private val onSetNotifications: (notificationTypes: Set<SubjectNotificationType>, onError: () -> Unit) -> Unit
) {
    private val subjectId = subscription.subjectId
    private val initialActiveNotifications: Set<SubjectNotificationType> =
        subscription.toActiveNotificationTypes(
            notificationGroup
        )

    val emptyTextViewModel: NotificationInfoViewModel.Text = NotificationInfoViewModel.Text.Empty

    val infoIconsViewModel: ObservableField<NotificationInfoViewModel.Icons> = ObservableField(
        initialActiveNotifications.toIconViewModels().toNotificationInfoViewModel()
    )

    val hasIcons: ObservableBoolean = ObservableBoolean(initialActiveNotifications.isNotEmpty())

    private var activeNotifications: Set<SubjectNotificationType> = initialActiveNotifications
        set(value) {
            if (field != value) {
                infoIconsViewModel.set(value.toIconViewModels().toNotificationInfoViewModel())
                hasIcons.set(value.isNotEmpty())
                isSwitchToggled.set(value.isNotEmpty())
                field = value
            }
        }

    val isSwitchToggled: ObservableBoolean = ObservableBoolean(activeNotifications.isNotEmpty())

    @ExperimentalTime
    val onSwitchStateChanged = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isSwitchToggled.get() == isChecked) {
            return@OnCheckedChangeListener
        }

        setNotificationsAsync(
            notificationTypes = if (isChecked) {
                notificationGroup.toDefaultNotificationTypes()
            } else emptySet()
        )
    }

    val onSettingsClicked = View.OnClickListener {
        onSubjectNotificationTypesClicked(
            SubjectNotificationTypesAction(
                name = name,
                subjectId = subjectId,
                possibleNotifications = notificationGroup.notificationTypes,
                initialActiveNotifications = activeNotifications
            )
        )
    }

    @ExperimentalTime
    private fun setNotificationsAsync(
        notificationTypes: Set<SubjectNotificationType>
    ) {
        val previousNotifications = activeNotifications
        activeNotifications = notificationTypes

        onSetNotifications(notificationTypes) {
            activeNotifications = previousNotifications
        }
    }
}