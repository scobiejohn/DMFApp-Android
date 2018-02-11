package au.com.brightcapital.utils

import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun steppedNumber(step: Double, value: Double, min: Int = 0): Int {
        return maxOf((round(value/step) * step), min.toDouble()).toInt()
    }

    fun getUserDateSince(selectedPeriod:String): String {
        val today = Date()
        val cal = Calendar.getInstance()
        cal.time = today
        val dateFormatter = SimpleDateFormat("yyyyMMdd")
        dateFormatter.timeZone = TimeZone.getTimeZone(cal.timeZone.displayName)

        when (selectedPeriod) {
            Constants.ReportingOneMonth -> cal.add(Calendar.DATE, -31)
            Constants.ReportingThreeMonths -> cal.add(Calendar.DATE, -90)
            Constants.ReportingTwelveMonths -> cal.add(Calendar.DATE, -360)
            Constants.ReportingThirtySixMonths -> cal.add(Calendar.DATE, -1080)
            Constants.ReportingSixtyMonths -> cal.add(Calendar.DATE, -1800)
            Constants.ReportingHundredTwentyMonths -> cal.add(Calendar.DATE, -3600)
            Constants.ReportingJuneToDate -> {
                var dateString = ""
                if (cal.get(Calendar.MONTH) >= 7) {
                    dateString = cal.get(Calendar.YEAR).toString() + "0630"
                } else {
                    dateString = (cal.get(Calendar.YEAR) - 1).toString() + "0630"
                }
                return dateString
            }
            Constants.ReportingYearToDate -> {
                return cal.get(Calendar.YEAR).toString() + "0101"
            }
            else -> cal.add(Calendar.DATE, -30000)
        }

        return dateFormatter.format(cal!!.time)
    }

}