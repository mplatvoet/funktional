package nl.mplatvoet.funktional.examples

import nl.mplatvoet.funktional.types.*
import java.util.LinkedList


fun main(args: Array<String>) {
    println(Try.of(20) bind halfFn bind halfFn bind halfFn)

    println(divide(4, 2))
    println(divide(3, 0))

    val fn: (Int) -> Int = { it + 3 }
    println(divide(4, 2).right() map fn)
    println(divide(3, 0).right() map fn)

    val (l, r) = divide(6,3)
    println(r map fn)
}

val halfFn =
        fun (a: Int): Try<Int> {
            return if (a and 1 == 1) Failure(IllegalStateException("$a is uneven")) else Success(a / 2)
        }

fun divide(x: Int, y: Int): Either<String, Int> =
        if (y == 0) Either.ofLeft("$x/$y") else Either.ofRight(x / y)

