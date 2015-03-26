package nl.mplatvoet.funktional.types

public trait Functor<A : Any> {
    fun map<B : Any>(fn: (A) -> B): Functor<B>
}

public trait Monad<A : Any> {
    fun bind<B : Any>(fn: (A) -> Monad<B>): Monad<B>
}

public trait Applicative<A : Any> {
    fun apply<B : Any>(f: Functor<(A) -> B>): Functor<B>
}


public open class Just<A>(val value: A) : Functor<A> {
    override fun <B : Any> map(fn: (A) -> B): Just<B> = Just(fn(value))
}

public trait Option<A : Any> : Functor<A> {
    companion object {
        fun of<A : Any>(value: A?): Option<A> = if (value == null) None() else Some(value)
    }

    override fun map<B : Any>(fn: (A) -> B): Option<B>
}

public class None<A> : Option<A> {
    override fun <B : Any> map(fn: (A) -> B): Option<B> = None()
}

public class Some<A>(value: A) : Just<A>(value), Option<A> {
    override fun <B : Any> map(fn: (A) -> B): Some<B> = Some(fn(value))
}


public trait Either<A : Any, B : Any> : Functor<B> {
    companion object {
        fun <A : Any, B : Any> left(value: A) = Left<A, B>(value)
        fun <A : Any, B : Any> right(value: B) = Right<A, B>(value)
    }

    override fun map<C : Any>(fn: (B) -> C): Either<A, C>
}

public open class Left<A : Any, B : Any>(val value: A) : Either<A, B> {
    override fun map<C : Any>(fn: (B) -> C): Left<A, C> = Left<A, C>(value)
}

public open class Right<A : Any, B : Any>(val value: B) : Either<A, B> {
    override fun map<C : Any>(fn: (B) -> C): Right<A, C> = Right<A, C>(fn(value))
}

public trait Try<A : Any> : Either<Throwable, A> {
    override fun map<B : Any>(fn: (A) -> B): Try<B>
}

public class Success<A : Any>(value: A) : Right<Throwable, A> (value), Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Success<B> = Success(fn(value))
}

public class Failure<A : Any>(value: Throwable) : Left<Throwable, A> (value), Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Failure<B> = Failure(value)
}