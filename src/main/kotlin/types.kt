package nl.mplatvoet.funktional.types

public trait Functor<A : Any> {
    fun map<B : Any>(fn: (A) -> B): Functor<B>
}

public trait Applicative<A : Any> {
    fun apply<B : Any>(f: Functor<(A) -> B>): Functor<B>
}


public open class Just<A>(val value: A) : Functor<A> {
    override fun <B : Any> map(fn: (A) -> B): Just<B> = Just(fn(value))
    fun <B : Any> bind(fn: (A) -> Just<B>): Just<B> = fn(value)
}

public trait Option<A : Any> : Functor<A> {
    companion object {
        fun of<A : Any>(value: A?): Option<A> = if (value == null) None() else Some(value)
    }

    override fun map<B : Any>(fn: (A) -> B): Option<B>
    fun bind<B : Any>(fn: (A) -> Option<B>): Option<B>
}

public class None<A> : Option<A> {
    override fun <B : Any> map(fn: (A) -> B): Option<B> = None()
    override fun bind<B : Any>(fn: (A) -> Option<B>): None<B> = None()
}

public open class Some<A>(value: A) : Just<A>(value), Option<A> {
    override fun <B : Any> map(fn: (A) -> B): Some<B> = Some(fn(value))
    override fun bind<B : Any>(fn: (A) -> Option<B>): Option<B> = fn(value)
}


public trait Either<A : Any, B : Any> {
    companion object {
        fun <A : Any, B : Any> left(value: A) = Left<A, B>(value)
        fun <A : Any, B : Any> right(value: B) = Right<A, B>(value)
    }
}

public open class Left<A : Any, B : Any>(val value: A) : Either<A, B>, Functor<A>  {
    override fun <C : Any> map(fn: (A) -> C): Left<C, B> = Left<C,B>(fn(value))
}

public open class Right<A : Any, B : Any>(value: B) : Either<A, B>, Some<B>(value) {
    override fun map<C : Any>(fn: (B) -> C): Right<A, C> = Right<A, C>(fn(value))
}

public trait Try<A : Any> : Functor<A> {
    override fun map<B : Any>(fn: (A) -> B): Try<B>
}

public class Success<A : Any>(value: A) : Just<A>(value), Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Success<B> = Success(fn(value))
}

public class Failure<A : Any>(val value: Throwable) : Try<A> {
    override fun <B : Any> map(fn: (A) -> B): Failure<B> = Failure(value)
}