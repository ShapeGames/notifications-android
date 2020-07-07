package dk.shape.games.notifications.utils

import java.util.*

inline fun <reified T : Enum<*>> enumValueOrNull(name: String): T? =
    T::class.java.enumConstants.firstOrNull { enumValue ->
        enumValue.name.toLowerCase(Locale.ROOT) == name
    }