package y2k.joyreactor.services.repository

import java.util.*

/**
 * Created by y2k on 12/23/15.
 */
class ArrayListDataSet<T : Dto>(val name: String) : DataSet<T> {

    private val items = ArrayList<T>()

    override fun clear() {
        items.clear()
    }

    override fun remove(element: T) {
        items.remove(element)
    }

    override fun add(element: T): T {
        val id = (Math.random() * Long.MAX_VALUE).toLong()
        @Suppress("UNCHECKED_CAST")
        val e = if (element.id == 0L) element.identify(id)  as T else element
        items.add(e)
        return e
    }

    override fun filter(f: (T) -> Boolean): List<T> {
        return items.filter(f)
    }

    override fun firstOrNull(f: (T) -> Boolean): T? {
        return items.firstOrNull(f)
    }

    override fun toList(): List<T> {
        return items.toList()
    }

    override fun forEach(f: (T) -> Unit) {
        items.forEach(f)
    }

    override fun none(f: (T) -> Boolean): Boolean {
        return items.none(f)
    }

    override fun asIterable(): Iterable<T> {
        return items.asIterable()
    }

    override fun first(f: (T) -> Boolean): T {
        return items.first(f)
    }

    override fun <K> groupBy(f: (T) -> K): Map<K, List<T>> {
        return items.groupBy(f)
    }
}