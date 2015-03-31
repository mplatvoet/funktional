package nl.mplatvoet.funktional.examples

import nl.mplatvoet.funktional.types.*
import nl.mplatvoet.funktional.types.List
import java.util.LinkedList


fun main(args: Array<String>) {
    println(Try.of(20) bind halfFn bind halfFn bind halfFn)

    println(divide(4, 2))
    println(divide(3, 0))

    val fn: (Int) -> Int = { it + 3 }
    println(divide(4, 2).right() map fn)
    println(divide(3, 0).right() map fn)


    val list = List.of(1, 2, 3, 4, 5, 6) map { it * it }
    println(list)
}

val halfFn =
        fun (a: Int): Try<Int> {
            return if (a and 1 == 1) Failure(IllegalStateException("$a is uneven")) else Success(a / 2)
        }

fun divide(x: Int, y: Int): Either<String, Int> =
        if (y == 0) Either.ofLeft("$x/$y") else Either.ofRight(x / y)

