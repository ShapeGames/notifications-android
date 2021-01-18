package dk.shape.games.notifications.actions

import dk.shape.games.sportsbook.offerings.common.action.Action
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationSettingsAction(
    val config: NotificationSettingsActionConfig = NotificationSettingsActionConfig.Default
) : Action {

    override fun isModal() = true

    override fun isQuickBetEnabled() = false
}

