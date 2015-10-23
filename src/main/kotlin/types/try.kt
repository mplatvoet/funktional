package nl.mplatvoet.funktional.types

public interface Try<A : Any> : Functor<A> {
    companion object {
        fun <A : Any> of(value: A) : Try<A> = Success(value)
    }

    override infix fun <B : Any> map(fn: (A) -> B): Try<B>
    infix fun <B : Any> bind(fn: (A) -> Try<B>): Try<B>
}

public data class Success<A : Any>(val value: A) : Try<A> {
    override infix fun <B : Any> map(fn: (A) -> B): Success<B> = Success(fn(value))
    override infix fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = fn(value)
    override fun toString(): String = "[Success $value]"
}

public data class Failure<A : Any>(val value: Throwable) : Try<A> {
    @Suppress("CAST_NEVER_SUCCEEDS")
    override infix fun <B : Any> map(fn: (A) -> B): Failure<B> = this as Failure<B>

    @Suppress("CAST_NEVER_SUCCEEDS")
    override infix fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = this as Failure<B>

    override fun toString(): String {
        val errorMsg = value.message
        return "[Failure ${if (errorMsg != null && errorMsg.isNotEmpty()) "\"$errorMsg\"" else value.javaClass.simpleName}]"
    }
}

