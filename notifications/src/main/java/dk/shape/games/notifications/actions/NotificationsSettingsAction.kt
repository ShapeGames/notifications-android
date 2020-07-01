package dk.shape.games.notifications.actions

import dk.shape.games.sportsbook.offerings.common.action.Action
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationsSettingsAction(
    val betslipComponentUUID: String? = null,
    val openedFromMyGames: Boolean = false
) : Action {

    override fun isModal() = true

    override fun isQuickBetEnabled() = false

    override fun hashCode() = toString().toInt()
}
