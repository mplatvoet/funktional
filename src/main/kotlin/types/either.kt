package nl.mplatvoet.funktional.types



public sealed class Either<A : Any, B : Any> : Functor<B> {
    companion object {
        fun <A : Any, B : Any> ofLeft(value: A) = Left<A, B>(value)
        fun <A : Any, B : Any> ofRight(value: B) = Right<A, B>(value)
    }

    abstract override fun <C : Any> map(fn: (B) -> C): Either<A, C>

    abstract operator fun component1(): Maybe<A>
    abstract operator fun component2(): Maybe<B>

    abstract fun isLeft(): Boolean
    abstract fun isRight(): Boolean

    abstract fun left(): Maybe<A>
    abstract fun right(): Maybe<B>

    abstract fun swap(): Either<B, A>

    public class Left<A : Any, B : Any>(val value: A) : Either<A, B>() {
        override fun <C : Any> map(fn: (B) -> C): Either<A, C> = Left(value)
        override fun toString(): String = "[Left $value]"

        operator override fun component1(): Maybe<A> = left()
        operator override fun component2(): Maybe<B> = right()

        override fun isLeft(): Boolean = true
        override fun isRight(): Boolean = false

        override fun left(): Maybe<A> = Maybe.Just(value)
        override fun right(): Maybe<B> = Maybe.Nothing.of()

        override fun swap(): Either<B, A> = Either.ofRight(value)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Left<*, *>) return false

            if (this.hashCode() != other.hashCode()) return false
            return this.value == other.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    public open class Right<A : Any, B : Any>(val value: B) : Either<A, B>() {
        override fun <C : Any> map(fn: (B) -> C): Right<A, C> = Right<A, C>(fn(value))
        override fun toString(): String = "[Right $value]"

        override fun component1(): Maybe<A> = left()
        override fun component2(): Maybe<B> = right()

        override fun isLeft(): Boolean = false
        override fun isRight(): Boolean = true

        override fun left(): Maybe<A> = Maybe.Nothing.of()
        override fun right(): Maybe<B> = Maybe.Just(value)

        override fun swap(): Either<B, A> = Either.ofLeft(value)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Right<*, *>) return false

            if (this.hashCode() != other.hashCode()) return false
            return this.value == other.value
        }

        override fun hashCode(): Int = value.hashCode()
    }
}

