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


public open class Just<A>(val value: A) : Functor<A>,  Monad<A> {
    override fun <B : Any> map(fn: (A) -> B): Just<B> = Just(fn(value))
    override fun <B : Any> bind(fn: (A) -> Monad<B>): Just<B> = bindMonad(value, fn)
}

public trait Option<A : Any> : Functor<A>, Monad<A> {
    companion object {
        fun of<A : Any>(value: A?): Option<A> = if (value == null) None() else Some(value)
    }

    override fun map<B : Any>(fn: (A) -> B): Option<B>
    override fun bind<B : Any>(fn: (A) -> Monad<B>): Option<B>
}

public class None<A> : Option<A> {
    override fun <B : Any> map(fn: (A) -> B): Option<B> = None()
    override fun bind<B : Any>(fn: (A) -> Monad<B>): None<B> = None()
}

public open class Some<A>(value: A) : Just<A>(value), Option<A> {
    override fun <B : Any> map(fn: (A) -> B): Some<B> = Some(fn(value))
    override fun bind<B : Any>(fn: (A) -> Monad<B>): Some<B> = bindMonad(value, fn)
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

public open class Right<A : Any, B : Any>(value: B) : Either<A, B>, Some<B>(value) {
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

private inline fun <A:Any, B:Any, reified T:Monad<B>> bindMonad(value: A, fn: (A) -> Monad<B>): T {
    val monad = fn(value)
    return when (monad) {
        is T -> monad
        else -> throw IllegalStateException("Resulting Monad is not of type ${javaClass<T>()} but ${monad.javaClass}")
    }
}