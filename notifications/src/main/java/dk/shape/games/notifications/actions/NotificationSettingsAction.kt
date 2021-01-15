package dk.shape.games.notifications.actions

import dk.shape.games.sportsbook.offerings.common.action.Action
import kotlinx.android.parcel.Parcelize

sealed class NotificationSettingsAction : Action {

    @Parcelize
    data class NotificationsForAllEventIds(
        val eventIds: List<String> = emptyList()
    ) : NotificationSettingsAction()

    @Parcelize
    object FromMyGames : NotificationSettingsAction()

    @Parcelize
    data class WithBetSlipComponentUUID(
        val betSlipComponentUUID: String? = null
    ) : NotificationSettingsAction()

    @Parcelize
    object NoData: NotificationSettingsAction()

    override fun isModal() = true

    override fun isQuickBetEnabled() = false
}

