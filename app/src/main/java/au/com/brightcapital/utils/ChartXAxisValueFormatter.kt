package au.com.brightcapital.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class ChartXAxisValueFormatter(private val dates: ArrayList<Date>): IAxisValueFormatter {

    private val formatter = SimpleDateFormat("MMM dd")

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return formatter.format(dates[value.toInt()])
    }

}