package dev.civmc.bumhug.util

fun levelExpToPoints(level: Int, exp: Float): Int {
    // good luck
    var a = 1f
    var b = 6f
    var c = 0f
    var x = 2f
    var y = 7f
    if (level > 16 && level <= 31) {
        a = 2.5f
        b = -40.5f
        c = 360f
        x = 5f
        y = -38f
    } else if (level >= 32) {
        a = 4.5f
        b = -162.5f
        c = 2220f
        x = 9f
        y = -158f
    }
    return Math.floor((a * level * level + b * level + c + exp * (x * level + y)).toDouble()).toInt()
}
