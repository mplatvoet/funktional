package nl.mplatvoet.funktional.types

trait Seq<A : Any> : Functor<A> {

}

class List<A : Any> : Seq<A> {
    companion object {
        fun of<A : Any>(vararg values: A): List<A> {
            var head = List<A>()
            values.reverse().forEach { a ->
                head = head.prepend(a)
            }
            return head
        }
        fun of<A : Any>(values: Iterable<A>): List<A> {
            var head = List<A>()
            values.reverse().forEach { a ->
                head = head.prepend(a)
            }
            return head
        }
    }

    private val value: A?
    private val rest: List<A>?

    constructor() : this(null, null) {
    }

    constructor(value: A) : this(value, null) {
    }

    private constructor(value: A?, rest: List<A>?) {
        this.value = value
        this.rest = rest
    }


    override fun <B : Any> fmap(fn: (A) -> B): List<B> {
        var head = List<B>()
        foldr { a -> head = head.prepend(fn(a)) }
        return head
    }

    fun prepend(value: A): List<A> = List(value, this)

    fun fold(fn: (A) -> Unit) = foldl(fn)

    fun foldl(fn: (A) -> Unit) {
        var elem: List<A>? = this
        do {
            val current = elem!!.value
            if (current != null) {
                fn (current)
            }
            elem = elem!!.rest
        } while (elem != null)
    }

    fun foldr(fn: (A) -> Unit) {
        rest?.foldr(fn)
        if (value != null) fn(value)
    }
}
