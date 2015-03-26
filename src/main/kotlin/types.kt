package nl.mplatvoet.funktional.types

public trait Functor<A : Any> {
    fun map<B>(fn: (A) -> B): Functor<B>
}

public trait Monad<A : Any> {
    fun bind<B>(fn: (A) -> Functor<B>): Functor<B>
}

public trait Applicative<A : Any> {
    fun apply<B>(f: Functor<(A) -> B>): Functor<B>
}


public open class Just<A>(val value: A) : Functor<A> {
    override fun <B> map(fn: (A) -> B): Functor<B> = Just(fn(value))
}

public trait Option<A> : Functor<A> {
    companion object {
        fun of<A : Any>(value: A?): Option<A> = if (value == null) None() else Some(value)
    }

    override fun map<B>(fn: (A) -> B): Option<B>
}

public class None<A> : Option<A> {
    override fun <B> map(fn: (A) -> B): Option<B> = None()
}

public class Some<A>(value: A) : Just<A>(value), Option<A> {
    override fun <B> map(fn: (A) -> B): Option<B> = Some(fn(value))
}


public trait Either<A, B> : Functor<B> {
    companion object {
        fun <A, B> left(value: A) = Left<A, B>(value)
        fun <A, B> right(value: B) = Right<A, B>(value)
    }

    override fun map<C>(fn: (B) -> C): Either<A, C>
}

public open class Left<A, B>(val value: A) : Either<A, B> {
    override fun map<C>(fn: (B) -> C): Either<A, C> = Left<A, C>(value)
}

public open class Right<A, B>(val value: B) : Either<A, B> {
    override fun map<C>(fn: (B) -> C): Either<A, C> = Right<A, C>(fn(value))
}

public trait Try<A> : Either<Throwable, A> {
    override fun map<B>(fn: (A) -> B): Try<B>
}

public class Success<A>(value: A) : Right<Throwable, A> (value), Try<A> {
    override fun <B> map(fn: (A) -> B): Try<B> = try {
        Success(fn(value))
    } catch (t: Throwable) {
        Failure(t)
    }
}

public class Failure<A>(value: Throwable) : Left<Throwable, A> (value), Try<A> {
    override fun <B> map(fn: (A) -> B): Try<B> = Failure(value)
}