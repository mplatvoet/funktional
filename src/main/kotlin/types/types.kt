package nl.mplatvoet.funktional.types

public trait Functor<A : Any> {
    fun fmap<B : Any>(fn: (A) -> B): Functor<B>
}

/*
    No distinct applicative trait is used since it can't be properly typed in
    Java/Kotlin. For instance, a Maybe monad should only accept functions
    wrapped in Maybe monad types. This can't be forced compile time when a common
    super type is used.
*/

/*
    No distinct monad trait is used since it can't be properly typed in
    Java/Kotlin. For instance, a Try monad should only accept functions
    returning Try monad types. This can't be forced compile time when a common
    super type is used.
*/


