package au.com.dmf.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.NumberFormat

/**
 * Created by raymond on 15/10/17.
 */
class ChartYAxisValueFormatter: IAxisValueFormatter {

    private var numFormatter: NumberFormat = NumberFormat.getCurrencyInstance()

    init {
        numFormatter.minimumFractionDigits = 0
        numFormatter.maximumFractionDigits = 0
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return "AUD +" + numFormatter.format(value)
    }

}