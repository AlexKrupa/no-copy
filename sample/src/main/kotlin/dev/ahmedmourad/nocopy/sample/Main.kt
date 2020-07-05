package dev.ahmedmourad.nocopy.sample

data class PhoneNumber private constructor(
        val value: String
) {

// uncommenting this should remove the error
//    fun copy(value: String): Unit {
//
//    }

    companion object {
        fun of(value: String): PhoneNumber {
            return PhoneNumber(value)
        }
    }
}

fun main() {
    println(PhoneNumber.of("+201234567890").copy(value = "Happy Birthday!"))
}
