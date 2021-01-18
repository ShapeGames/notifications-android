package dk.shape.games.notifications.actions

import dk.shape.games.sportsbook.offerings.common.action.Action
import kotlinx.android.parcel.Parcelize

sealed class NotificationSettingsAction : Action {

    @Parcelize
    data class IncludeAllEvents(
        val eventIds: List<String>
    ) : NotificationSettingsAction()

    @Deprecated("This will not be used with a release of new BetHistory")
    @Parcelize
    object FilterBetEvents : NotificationSettingsAction()

    @Parcelize
    data class FilterBetSlip(
        val betSlipComponentUUID: String? = null
    ) : NotificationSettingsAction()

    @Parcelize
    object Default: NotificationSettingsAction()

    override fun isModal() = true

    override fun isQuickBetEnabled() = false
}

