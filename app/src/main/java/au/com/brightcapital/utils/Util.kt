package au.com.brightcapital.utils

import java.lang.Math.round

object Util {

    fun steppedNumber(step: Double, value: Double, min: Int = 0): Int {
        return maxOf((round(value/step) * step), min.toDouble()).toInt()
    }

}