package nl.mplatvoet.funktional.types

public trait Functor<A : Any> {
    fun map<B : Any>(fn: (A) -> B): Functor<B>
}

public trait Applicative<A : Any> {
    fun apply<B : Any>(f: Functor<(A) -> B>): Functor<B>
}


public trait Maybe<A : Any> : Functor<A>, Applicative<A> {
    companion object {
        fun of<A : Any>(value: A?): Maybe<A> = if (value == null) Nothing() else Just(value)
    }

    override fun map<B : Any>(fn: (A) -> B): Maybe<B>
    fun bind<B : Any>(fn: (A) -> Maybe<B>): Maybe<B>
}

public class Nothing<A> : Maybe<A> {
    override fun <B : Any> apply(f: Functor<(A) -> B>): Maybe<B> = Nothing()
    override fun <B : Any> map(fn: (A) -> B): Maybe<B> = Nothing()
    override fun bind<B : Any>(fn: (A) -> Maybe<B>): Nothing<B> = Nothing()
    override fun toString(): String = "[Nothing]"
}

public open class Just<A>(val value: A) : Maybe<A> {
    override fun <B : Any> apply(f: Functor<(A) -> B>): Maybe<B> = when(f) {
        is Just -> Just(f.value(value))
        else -> Nothing()
    }
    override fun <B : Any> map(fn: (A) -> B): Just<B> = Just(fn(value))
    override fun bind<B : Any>(fn: (A) -> Maybe<B>): Maybe<B> = fn(value)
    override fun toString(): String = "[Just ${value}]"
}


public trait Either<A : Any, B : Any> : Functor<B> {
    companion object {
        fun <A : Any, B : Any> left(value: A) = Left<A, B>(value)
        fun <A : Any, B : Any> right(value: B) = Right<A, B>(value)
    }

    override fun <C : Any> map(fn: (B) -> C): Either<A, C>
}

public open class Left<A : Any, B : Any>(val value: A) : Either<A, B> {
    override fun <C : Any> map(fn: (B) -> C): Either<A, C> = Left(value)
    override fun toString(): String = "[Left ${value}]"
}

public open class Right<A : Any, B : Any>(value: B) : Either<A, B>, Just<B>(value) {
    override fun map<C : Any>(fn: (B) -> C): Right<A, C> = Right<A, C>(fn(value))
    override fun toString(): String = "[Right ${value}]"
}

public trait Try<A : Any> : Functor<A> {
    override fun map<B : Any>(fn: (A) -> B): Try<B>
    fun bind<B : Any>(fn: (A) -> Try<B>): Try<B>
}

public class Success<A : Any>(value: A) : Just<A>(value), Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Success<B> = Success(fn(value))
    override fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = fn(value)
    override fun toString(): String = "[Success ${value}]"
}

public class Failure<A : Any>(val value: Throwable) : Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Failure<B> = Failure(value)
    override fun <B : Any> bind(fn: (A) -> Try<B>): Try<B> = Failure(value)
    override fun toString(): String = "[Failure ${value.getMessage() ?: value.javaClass.getSimpleName()}]"
}