package nl.mplatvoet.funktional.types


public interface Maybe<A : Any> : Functor<A> {
    companion object {
        fun <A : Any> of(value: A?): Maybe<A> = if (value == null) Nothing.of() else Just(value)
    }

    override fun <B : Any> map(fn: (A) -> B): Maybe<B>
    infix fun <B : Any> bind(fn: (A) -> Maybe<B>): Maybe<B>
    infix fun <B : Any> apply(f: Maybe<(A) -> B>): Maybe<B>
}

public class Nothing<A : Any> : Maybe<A> {
    companion object {
        private val instance = Nothing<Any>()

        @Suppress("CAST_NEVER_SUCCEEDS")
        fun <A : Any> of(): Nothing<A> = instance as Nothing<A>
    }

    private constructor() {
    }

    override infix fun <B : Any> apply(f: Maybe<(A) -> B>): Maybe<B> = Nothing()
    override infix fun <B : Any> map(fn: (A) -> B): Maybe<B> = Nothing()
    override infix fun <B : Any> bind(fn: (A) -> Maybe<B>): Nothing<B> = Nothing()
    override fun toString(): String = "[Nothing]"
}


public data class Just<A : Any>(val value: A) : Maybe<A> {
    override fun <B : Any> apply(f: Maybe<(A) -> B>): Maybe<B> = when (f) {
        is Just -> Just(f.value(value))
        else -> Nothing.of()
    }

    override infix fun <B : Any> map(fn: (A) -> B): Just<B> = Just(fn(value))
    override infix fun <B : Any> bind(fn: (A) -> Maybe<B>): Maybe<B> = fn(value)
    override fun toString(): String = "[Just ${value}]"
}


