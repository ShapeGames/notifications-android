package dk.shape.games.notifications.usecases

import dk.shape.danskespil.foundation.DSApiResponseException
import dk.shape.danskespil.foundation.data.GamesDataResult
import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.repositories.NotificationsDataSource
import dk.shape.games.sportsbook.offerings.generics.event.data.EventsRepository
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

internal class NotificationsInteractor(
    private val notificationsDataSource: NotificationsDataSource,
    private val eventsRepository: EventsRepository,
    private val provideDeviceId: suspend () -> String,
    private val provideBetEventIds: suspend () -> List<String>,
    private val provideNotifications: suspend () -> Notifications,
    private val includePlacements: Boolean,
    private val filterEventIds: List<String>?
) : NotificationsUseCases {

    private val mutableState: BroadcastChannel<NotificationsState> = BroadcastChannel(Channel.CONFLATED)
    override val state: Flow<NotificationsState> = mutableState.asFlow()

    override suspend fun loadNotifications() {
        try {
            withContext(Dispatchers.IO) {
                mutableState.sendBlocking(NotificationsState.Loading)
                val deviceId = provideDeviceId()
                notificationsDataSource.getSubscriptions(deviceId).apply {
                    collect { subscriptions ->
                        if(!notificationsDataSource.isUpdatingSubscriptions(deviceId)) {
                            val subscriptionEventIds = subscriptions.map { it.eventId }
                            val betEventIdsWithoutSubscription = if (includePlacements) {
                                provideBetEventIds().filter { !subscriptionEventIds.contains(it) }
                            } else {
                                emptyList()
                            }
                            val eventIdsToShow =
                                (subscriptionEventIds + betEventIdsWithoutSubscription).filter {
                                    filterEventIds?.contains(it) ?: true
                                }
                            val joinedEventIds = eventIdsToShow.joinToString(",")
                            val notifications = provideNotifications()
                            val events = try {
                                if (joinedEventIds.isNotEmpty()) {
                                    val data = eventsRepository.getData(joinedEventIds)
                                    when (data) {
                                        is GamesDataResult.Success -> data.value
                                        else -> emptyList()
                                    }
                                } else emptyList()
                            } catch (e: Exception) {
                                if (e is DSApiResponseException.MissingBodyError) {
                                    emptyList<Event>()
                                } else throw e
                            }.filter { event ->
                                // Only those events that have some default notifications in the configuration
                                // are valid to show
                                notifications.group
                                    .find { it.groupId == event.notificationConfigurationId } != null
                            }
                            if (events.isNotEmpty()) {
                                mutableState.sendBlocking(NotificationsState.Content(events.toSet()))
                            } else {
                                mutableState.sendBlocking(NotificationsState.Empty)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            mutableState.sendBlocking(NotificationsState.Error)
        }
    }

}