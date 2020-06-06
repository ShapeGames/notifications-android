package dk.shape.games.notifications.demo.mock

import android.content.Context
import com.google.gson.GsonBuilder
import dk.shape.games.notifications.demo.R
import dk.shape.games.notifications.demo.context
import dk.shape.games.sportsbook.offerings.common.DBPublishService
import dk.shape.games.sportsbook.offerings.common.action.Action
import dk.shape.games.sportsbook.offerings.common.hierarchy.Level
import dk.shape.games.sportsbook.offerings.generics.search.data.SearchFilterSport
import dk.shape.games.sportsbook.offerings.generics.search.data.SearchModuleGroup
import dk.shape.games.sportsbook.offerings.modules.ModuleAction
import dk.shape.games.sportsbook.offerings.modules.banner.data.Banner
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.event.data.EventDeserializer
import dk.shape.games.sportsbook.offerings.modules.eventgroup.data.EventGroup
import dk.shape.games.sportsbook.offerings.modules.table.Table
import dk.shape.games.sportsbook.offerings.modules.virtuals.Schedule
import okhttp3.Request
import org.jetbrains.annotations.Nullable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun event(eventId: String): Event {
    val resourceId = when (eventId) {
        "event1" -> R.raw.event_one
        "event2" -> R.raw.event_two
        "event3" -> R.raw.event_three
        else -> R.raw.event_four
    }

    return GsonBuilder()
        .registerTypeAdapter(Event::class.java, EventDeserializer())
        .create()
        .fromJson(
            context.readResource(
                resourceId
            ), Event::class.java
        )
}

class MockEventService : DBPublishService {
    override fun getEvent(eventId: String?): Call<Event> {
        val event = event(eventId!!)

        return object : Call<Event> {
            override fun enqueue(callback: Callback<Event>) {
                callback.onResponse(this, Response.success(event))
            }

            override fun isExecuted() = true

            override fun clone() = this

            override fun isCanceled() = false

            override fun cancel() {
            }

            override fun execute() = Response.success(event)

            override fun request(): Request = Request.Builder().build()

        }
    }

    override fun search(
        query: String?,
        sportId: String?,
        filterMustHaveLiveVideoStream: Boolean,
        filterMustHaveTVCoverage: Boolean
    ): Call<SearchModuleGroup> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBannerFromDataSource(dataSource: String?): Call<Banner> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun levelHierarchy(): Call<MutableList<Level>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRetailEvent(eventId: String?): Call<Event> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEventsByIds(eventIds: String?): Call<MutableList<Event>> {
        val ids = eventIds!!.split(",")
        val events = ids.map { event(it) }

        return object : Call<MutableList<Event>> {
            override fun enqueue(callback: Callback<MutableList<Event>>) {
                callback.onResponse(this, Response.success(events.toMutableList()))
            }

            override fun isExecuted() = true

            override fun clone() = this

            override fun isCanceled() = false

            override fun cancel() {
            }

            override fun execute() = Response.success(events.toMutableList())

            override fun request(): Request = Request.Builder().build()

        }
    }

    override fun eventgroup(eventGroupId: String?): Call<EventGroup> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retailLevelHierarchy(): Call<MutableList<Level>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLevelHierarchy(levelHierarchyId: String?): Call<Level> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBanner(bannerId: String?): Call<Banner> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActionFromPossibleBet(betLevel: String?, betId: String?): Call<Action> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun eventgroups(eventGroupId: String?): Call<MutableList<EventGroup>> {
        TODO("not implemented")
    }

    override fun getScheduledEventGroupsForNavigation(navigationId: String?): Call<Schedule> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTables(tableId: String?): Call<Table> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActions(actionListId: String?): Call<MutableList<ModuleAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sports(): Call<MutableList<@Nullable SearchFilterSport?>>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun liveLevelHierarchy(): Call<MutableList<Level>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun Context.readResource(resourceId: Int): String {
    val inputStream = resources.openRawResource(resourceId)

    val b = ByteArray(inputStream.available())
    inputStream.read(b)
    return String(b)
}
