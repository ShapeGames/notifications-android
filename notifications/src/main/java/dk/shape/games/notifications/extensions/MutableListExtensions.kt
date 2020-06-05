package dk.shape.games.notifications.extensions

internal fun <T> MutableList<T>.updateWith(newItems: List<T>) {
    filterNot { newItems.contains(it) }.forEach { remove(it) }
    newItems.filterNot { contains(it) }.forEach { add(it) }
}