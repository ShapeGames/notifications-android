package dk.shape.games.notifications.presentation.viewmodels.settings

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.actions.NotificationSettingsSubjectAction
import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.extensions.toActiveNotificationTypes
import dk.shape.games.notifications.extensions.toDefaultNotificationTypes
import dk.shape.games.notifications.usecases.LoadedSubscription
import kotlin.time.ExperimentalTime

typealias OnSubjectNotificationTypesClicked = (NotificationSettingsSubjectAction) -> Unit
typealias OnSetSubjectNotifications = (notificationTypes: Set<SubjectNotificationType>, onError: () -> Unit) -> Unit

data class NotificationsSettingsSubjectViewModel(
    val name: String,
    private val subscription: Subscription,
    private val notificationGroup: SubjectNotificationGroup,
    private val onSubjectNotificationTypesClicked: OnSubjectNotificationTypesClicked,
    private val onSetNotifications: OnSetSubjectNotifications
) {
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
            NotificationSettingsSubjectAction(
                subjectName = name,
                subjectId = subscription.subjectId,
                subjectType = subscription.subjectType,
                possibleNotifications = notificationGroup.notificationTypes.toSet(),
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

internal fun LoadedSubscription.toNotificationsSettingsSubjectViewModel(
    onSubjectNotificationTypesClicked: OnSubjectNotificationTypesClicked,
    onSetNotifications: OnSetSubjectNotifications
) = NotificationsSettingsSubjectViewModel(
    name = name,
    subscription = subscription,
    notificationGroup = notificationGroup,
    onSubjectNotificationTypesClicked = onSubjectNotificationTypesClicked,
    onSetNotifications = onSetNotifications
)