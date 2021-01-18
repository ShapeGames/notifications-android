package dk.shape.games.notifications.actions

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class NotificationSettingsActionConfig: Parcelable {

    @Parcelize
    data class IncludeAllEvents(
        val eventIds: List<String>
    ) : NotificationSettingsActionConfig()

    @Deprecated("This will not be used with a release of new BetHistory")
    @Parcelize
    object FilterBetEvents : NotificationSettingsActionConfig()

    @Parcelize
    data class FilterBetSlip(
        val betSlipComponentUUID: String? = null
    ) : NotificationSettingsActionConfig()

    @Parcelize
    object Default: NotificationSettingsActionConfig()
}