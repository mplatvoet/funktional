package nl.mplatvoet.funktional.types

public interface Try<A : Any> : Functor<A> {
    companion object {
        fun of<A : Any>(value: A) : Try<A> = Success(value)
    }

    override fun map<B : Any>(fn: (A) -> B): Try<B>
    fun bind<B : Any>(fn: (A) -> Try<B>): Try<B>
}

public data class Success<A : Any>(val value: A) : Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Success<B> = Success(fn(value))
    override fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = fn(value)
    override fun toString(): String = "[Success ${value}]"
}

public data class Failure<A : Any>(val value: Throwable) : Try<A> {
    suppress("CAST_NEVER_SUCCEEDS")
    override fun <B : Any> map(fn: (A) -> B): Failure<B> = this as Failure<B>

    suppress("CAST_NEVER_SUCCEEDS")
    override fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = this as Failure<B>

    override fun toString(): String {
        val errorMsg = value.getMessage()
        return "[Failure ${if (errorMsg != null && errorMsg.isNotEmpty()) "\"${errorMsg}\"" else value.javaClass.getSimpleName()}]"
    }
}

