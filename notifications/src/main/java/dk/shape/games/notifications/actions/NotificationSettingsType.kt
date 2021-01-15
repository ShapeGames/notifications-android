package dk.shape.games.notifications.actions

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class NotificationSettingsType: Parcelable {

    @Parcelize
    object OpenFromMyGames: NotificationSettingsType()
    @Parcelize
    object NotFromMyGames: NotificationSettingsType()
    @Parcelize
    data class WithEventIds(val eventIds: List<String>): NotificationSettingsType()
}