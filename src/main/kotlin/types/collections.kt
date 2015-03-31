package nl.mplatvoet.funktional.types

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.LinkedList
import java.util.RandomAccess

trait Seq<A : Any> : Functor<A> {

}

class List<A : Any> : Seq<A>, kotlin.List<A> {
    companion object {
        fun of<A : Any>(vararg values: A): List<A> {
            var head = List<A>()
            for (i in (0..values.size() - 1).reversed()) {
                head = head prepend values[i]
            }
            return head
        }

        fun of<A : Any>(values: Iterable<A>): List<A> {
            if (values is List) {
                return values
            }
            if (values is kotlin.List && values is RandomAccess) {
                return fromRandomAccessList(values)
            }
            if (values is LinkedList) {
                return fromLinkedList(values)
            }
            return fromAnyIterable(values)
        }

        private fun fromAnyIterable<A>(values: Iterable<A>): List<A> {
            var head = List<A>()
            values.reverse().forEach { a ->
                head = head.prepend(a)
            }
            return head
        }

        private fun fromLinkedList<A>(values: LinkedList<A>): List<A> {
            var head = List<A>()
            if (values.isEmpty()) return head
            val iterator = values.listIterator(values.size())
            do {
                head = head.prepend(iterator.previous())
            } while (iterator.hasPrevious())

            return head
        }

        private fun fromRandomAccessList<A>(values: kotlin.List<A>): List<A> /*where T: RandomAccess*/ {
            var head = List<A>()
            if (values.isEmpty()) return head
            for (i in (0..values.size() - 1).reversed()) {
                head = head prepend values[i]
            }
            return head
        }
    }

    private volatile var arrayCache: WeakReference<ArrayList<A>>? = null

    val size: Int
    private val value: A?
    private val rest: List<A>?

    constructor() : this(null, null) {
    }

    constructor(value: A) : this(value, null) {
    }

    private constructor(value: A?, rest: List<A>?) {
        this.value = value
        this.rest = rest
        size = when {
            rest != null -> 1 + rest.size
            value != null -> 1
            else -> 0
        }
    }

    override fun size(): Int = size

    override fun isEmpty(): Boolean = size == 0

    override fun contains(o: Any?): Boolean {
        if (o == null) return false

        iterateLeft {
            if (it == o ) return true
        }
        return false
    }

    override fun iterator(): Iterator<A> = listIterator()

    override fun containsAll(c: Collection<Any?>): Boolean {
        if (this == c) return true
        if (c.isEmpty()) return true
        if (isEmpty()) return false

        c.forEach {
            if (it == null || !contains(it)) return false
        }
        return true;
    }

    override fun get(index: Int): A {
        if (index >= size || index < 0) throw IndexOutOfBoundsException("index: $index, size: $size")

        var cnt = 0
        iterateLeft {
            if (cnt == index) return it
            ++cnt
        }
        throw IllegalStateException("unreachable")
    }

    override fun indexOf(o: Any?): Int {
        if (o == null) return -1
        var cnt = 0
        iterateLeft {
            if (it == o) return cnt
            ++cnt
        }
        return -1
    }

    override fun lastIndexOf(o: Any?): Int = if (o == null) -1 else asRandomAccessList().lastIndexOf(o)

    override fun listIterator(): ListIterator<A> = WrappedListIterator(asRandomAccessList().listIterator())

    override fun listIterator(index: Int): ListIterator<A> = WrappedListIterator(asRandomAccessList().listIterator(index))

    override fun subList(fromIndex: Int, toIndex: Int): kotlin.List<A> = List.of(asRandomAccessList().subList(fromIndex, toIndex))


    override fun <B : Any> map(fn: (A) -> B): List<B> {
        var head = List<B>()
        foldr { a -> head = head.prepend(fn(a)) }
        return head
    }

    fun prepend(value: A): List<A> = List(value, this)

    fun fold(fn: (A) -> Unit) = foldl(fn)

    fun foldl(fn: (A) -> Unit) = iterateLeft(fn)

    fun foldr(fn: (A) -> Unit) {
        val list = asRandomAccessList()
        for (i in (0..list.size() - 1).reversed()) {
            fn(list[i])
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false;
        if (this.identityEquals(other)) return true
        if (other !is kotlin.List<*>) return false
        if (size != other.size()) return false

        val otherIter = other.iterator()
        iterateLeft {
            if (it != otherIter.next()) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var hashCode = 1;
        iterateLeft {
            hashCode = 31*hashCode + it.hashCode();
        }
        return hashCode
    }

    override fun toString(): String {
        if (size == 0) return "[List []]"
        val sb = StringBuilder("[List [")
        var cnt = 0
        fold {
            if (cnt > 0) sb.append(",")
            ++cnt
            if (cnt > 5) {
                sb.append("..")
                return@fold
            } else {
                sb.append(it)
            }
        }
        sb.append("]]")
        return sb.toString()
    }

    private fun asRandomAccessList(): ArrayList<A> {
        val cachedList = arrayCache?.get()
        if (cachedList == null) {
            val list = ArrayList<A>()
            iterateLeft { list add it }

            // don't care about concurrent updates, just don't block
            arrayCache = WeakReference(list)
            return list
        }
        return cachedList
    }

    private inline
    fun iterateLeft(fn: (A) -> Unit) {
        var elem: List<A>? = this
        do {
            val current = elem!!.value
            if (current != null) {
                fn (current)
            }
            elem = elem!!.rest
        } while (elem != null)
    }

    private class WrappedListIterator<A>(private val target: ListIterator<A>) : ListIterator<A> by target
}
