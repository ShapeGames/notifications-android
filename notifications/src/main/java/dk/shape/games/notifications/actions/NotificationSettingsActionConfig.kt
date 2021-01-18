package dk.shape.games.notifications.actions

sealed class NotificationSettingsActionConfig {

    data class IncludeAllEvents(
        val eventIds: List<String>
    ) : NotificationSettingsActionConfig()

    @Deprecated("This will not be used with a release of new BetHistory")
    object FilterBetEvents : NotificationSettingsActionConfig()

    data class FilterBetSlip(
        val betSlipComponentUUID: String? = null
    ) : NotificationSettingsActionConfig()

    object Default: NotificationSettingsActionConfig()
}