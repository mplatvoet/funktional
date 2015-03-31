package nl.mplatvoet.funktional.types


public trait Maybe<A : Any> : Functor<A> {
    companion object {
        fun of<A : Any>(value: A?): Maybe<A> = if (value == null) Nothing.of() else Just(value)
    }

    override fun map<B : Any>(fn: (A) -> B): Maybe<B>
    fun bind<B : Any>(fn: (A) -> Maybe<B>): Maybe<B>
    fun <B : Any> apply(f: Maybe<(A) -> B>): Maybe<B>
}

public class Nothing<A> : Maybe<A> {
    companion object {
        private val instance = Nothing<Any>()

        suppress("CAST_NEVER_SUCCEEDS")
        fun of<A>() : Nothing<A> = instance as Nothing<A>
    }
    private constructor() {}

    override fun <B : Any> apply(f: Maybe<(A) -> B>): Maybe<B> = Nothing()
    override fun <B : Any> map(fn: (A) -> B): Maybe<B> = Nothing()
    override fun bind<B : Any>(fn: (A) -> Maybe<B>): Nothing<B> = Nothing()
    override fun toString(): String = "[Nothing]"
}



public data class Just<A>(val value: A) : Maybe<A> {
    override fun <B : Any> apply(f: Maybe<(A) -> B>): Maybe<B> = when(f) {
        is Just -> Just(f.value(value))
        else -> Nothing.of()
    }
    override fun <B : Any> map(fn: (A) -> B): Just<B> = Just(fn(value))
    override fun bind<B : Any>(fn: (A) -> Maybe<B>): Maybe<B> = fn(value)
    override fun toString(): String = "[Just ${value}]"
}


