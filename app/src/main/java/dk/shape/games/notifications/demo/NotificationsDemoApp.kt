package dk.shape.games.sportsbook.offerings.demo

import android.app.Activity
import android.content.Context
import android.graphics.Color
import dk.shape.componentkit2.ComponentKit
import dk.shape.componentkit2.androidextensions.AndroidMainThreadExecutor
import dk.shape.games.demoskeleton.DemoApp
import dk.shape.games.demoskeleton.DemoConfig
import dk.shape.games.demoskeleton.Style
import dk.shape.games.notifications.demo.R
import dk.shape.games.notifications.demo.NotificationsScreens
import kotlin.time.ExperimentalTime
import java.util.*

lateinit var context: Context
private var activity: Activity? = null

class NotificationsDemoApp : DemoApp() {

    override fun onCreate() {
        super.onCreate()
        context = this
        ComponentKit.setMainExecutor(AndroidMainThreadExecutor())
    }

    @ExperimentalTime
    override val demoConfig: DemoConfig by lazy {
        DemoConfig(
            homeTitle = "Notifications",
            demoScreens = NotificationsScreens.screens,
            styles = listOf(
                Style(
                    name = "Oddset",
                    styleResourceId = R.style.OddsetSportsBookModuleStyle,
                    logoResourceId = R.drawable.logo_oddset,
                    navBarColor = Color.parseColor("#1644A2")
                ),
                Style(
                    name = "BetWarrior",
                    styleResourceId = R.style.BetWarriorSportsBookModuleStyle,
                    logoResourceId = R.drawable.logo_betwarrior,
                    navBarColor = Color.parseColor("#EF683F")
                )
            ),
            locales = listOf(
                Locale.ENGLISH,
                Locale("da", "DK"),
                Locale("es", "ES"),
                Locale("pt", "BR")
            )
        )
    }
}
