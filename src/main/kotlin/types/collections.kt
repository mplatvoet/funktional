package nl.mplatvoet.funktional.types

import java.util.ArrayList
import java.util.LinkedList
import java.util.RandomAccess

trait Seq<A : Any> : Functor<A> {

}

class List<A : Any> : Seq<A> {
    companion object {
        fun of<A : Any>(vararg values: A): List<A> {
            var head = List<A>()
            for (i in (0..values.size() - 1).reversed()) {
                head = head prepend values[i]
            }
            return head
        }

        fun of<A : Any>(values: Iterable<A>): List<A> {
            if (values is kotlin.List && values is RandomAccess) {
                return fromRandomAccessList(values)
            }
            if (values is LinkedList) {
                return fromLinkedList(values)
            }
            return fromAnyIterable(values)
        }

        private fun fromAnyIterable<A>(values: Iterable<A>) : List<A> {
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


    override fun <B : Any> map(fn: (A) -> B): List<B> {
        var head = List<B>()
        foldr { a -> head = head.prepend(fn(a)) }
        return head
    }

    fun prepend(value: A): List<A> = List(value, this)

    fun fold(fn: (A) -> Unit) = foldl(fn)

    fun foldl(fn: (A) -> Unit) = iterateLeft(fn)

    fun foldr(fn: (A) -> Unit) {
        //avoiding recursion, otherwise we might blow the stack
        //rather making a copy in a Random Access list
        val list = asRandomAccessList()
        for (i in (0..list.size() - 1).reversed()) {
            fn(list[i])
        }
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
        val list = ArrayList<A>()
        iterateLeft { list add it }
        return list
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
}
