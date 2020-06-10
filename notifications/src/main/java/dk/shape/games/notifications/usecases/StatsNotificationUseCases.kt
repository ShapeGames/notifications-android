package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.aliases.StatsNotificationType

internal interface StatsNotificationUseCases {

    suspend fun loadNotifications(onOntificationsLoaded: suspend (
        possibleTypes: List<StatsNotificationType>,
        activatedTypes: List<StatsNotificationType>
    ) -> Unit)

    suspend fun toggleMasterForSubject(enabled: Boolean, onEnabled: suspend () -> Unit)

    suspend fun saveNotificationPreferences(onSaved: suspend () -> Unit)
}