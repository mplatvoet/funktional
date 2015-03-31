package nl.mplatvoet.funktional.types


public trait Either<A : Any, B : Any> : Functor<B> {
    companion object {
        fun <A : Any, B : Any> ofLeft(value: A) = Left<A, B>(value)
        fun <A : Any, B : Any> ofRight(value: B) = Right<A, B>(value)
    }

    override fun <C : Any> map(fn: (B) -> C): Either<A, C>

    fun component1(): Maybe<A>
    fun component2(): Maybe<B>

    fun isLeft(): Boolean
    fun isRight(): Boolean

    fun left() : Maybe<A>
    fun right() : Maybe<B>
}

public class Left<A : Any, B : Any>(val value: A) : Either<A, B> {
    override fun <C : Any> map(fn: (B) -> C): Either<A, C> = Left(value)
    override fun toString(): String = "[Left ${value}]"

    override fun component1(): Maybe<A> = left()
    override fun component2(): Maybe<B> = right()

    override fun isLeft(): Boolean = true
    override fun isRight(): Boolean = false

    override fun left(): Maybe<A> = Just(value)
    override fun right(): Maybe<B> = Nothing.of()

    override fun equals(other: Any?): Boolean {
        if (this.identityEquals(other)) return true
        if (other !is Left<*, *>) return false

        if (this.hashCode() != other.hashCode()) return false
        return this.value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
}

public open class Right<A : Any, B : Any>(val value: B) : Either<A, B> {
    override fun map<C : Any>(fn: (B) -> C): Right<A, C> = Right<A, C>(fn(value))
    override fun toString(): String = "[Right ${value}]"

    override fun component1(): Maybe<A> = left()
    override fun component2(): Maybe<B> = right()

    override fun isLeft(): Boolean = false
    override fun isRight(): Boolean = true

    override fun left(): Maybe<A> = Nothing.of()
    override fun right(): Maybe<B> = Just(value)

    override fun equals(other: Any?): Boolean {
        if (this.identityEquals(other)) return true
        if (other !is Right<*, *>) return false

        if (this.hashCode() != other.hashCode()) return false
        return this.value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
}