package nl.mplatvoet.funktional.types

public sealed class Try<A : Any> : Functor<A> {
    companion object {
        fun <A : Any> of(value: A) : Try<A> = Success(value)
    }

    abstract override infix fun <B : Any> map(fn: (A) -> B): Try<B>
    abstract infix fun <B : Any> bind(fn: (A) -> Try<B>): Try<B>

    public class Success<A : Any>(val value: A) : Try<A>() {
        override infix fun <B : Any> map(fn: (A) -> B): Success<B> = Success(fn(value))
        override infix fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = fn(value)
        override fun toString(): String = "[Success $value]"

        override fun equals(other: Any?): Boolean{
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Success<*>
            return value == other.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    public class Failure<A : Any>(val value: Throwable) : Try<A>() {
        @Suppress("CAST_NEVER_SUCCEEDS")
        override infix fun <B : Any> map(fn: (A) -> B): Failure<B> = this as Failure<B>

        @Suppress("CAST_NEVER_SUCCEEDS")
        override infix fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = this as Failure<B>

        override fun toString(): String {
            val errorMsg = value.message
            return "[Failure ${if (errorMsg != null && errorMsg.isNotEmpty()) "\"$errorMsg\"" else value.javaClass.simpleName}]"
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Failure<*>
            return value == other.value
        }

        override fun hashCode(): Int = value.hashCode()
    }
}



