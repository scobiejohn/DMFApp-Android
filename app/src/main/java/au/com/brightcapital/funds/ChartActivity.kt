package au.com.brightcapital.funds

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import au.com.brightcapital.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_chart.*
import android.graphics.PorterDuff
import android.text.Html
import android.view.MenuItem
import au.com.brightcapital.data.FundsDataManager
import au.com.brightcapital.utils.ChartXAxisValueFormatter
import au.com.brightcapital.utils.ChartYAxisValueFormatter
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import java.text.SimpleDateFormat
import java.util.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChartActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }

    private var count = 0

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        upArrow.clearColorFilter()
        upArrow.setColorFilter(this.resources.getColor(R.color.colorWhite), PorterDuff.Mode.MULTIPLY)
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        supportActionBar?.title = Html.fromHtml("<font color='#ffffff'>Fund Chart</font>")

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        //fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //dummy_button.setOnTouchListener(mDelayHideTouchListener)

        renderChart()

    }

    private fun renderChart() {
        // if disabled, scaling can be done on x- and y-axis separately
        largeChart.setPinchZoom(true)
        //add data
        setData()

        val dates = ArrayList<Date>()
        for (idx in 0 until FundsDataManager.dmfHistoryData.size) {
            val o = FundsDataManager.dmfHistoryData[idx]
            dates.add(dateFormatShort.parse(o.HistoryDate!!))
        }

        largeChart.description = null
        largeChart.legend.formToTextSpace = 2f
        largeChart.legend.xEntrySpace = 24f
        largeChart.xAxis.granularity = 1.0f
        largeChart.xAxis.setLabelCount(minOf(dates.size, 16), true)
        largeChart.xAxis.setAvoidFirstLastClipping(true)

        //get the legend (only possible after setting data)
        val legend = largeChart.legend
        legend.form = Legend.LegendForm.SQUARE
        legend.textSize = 11f
        legend.textColor = Color.DKGRAY
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val xAxis = largeChart.xAxis
        xAxis.textSize = 11f
        xAxis.textColor = Color.DKGRAY
        xAxis.valueFormatter = ChartXAxisValueFormatter(dates)

        val leftAxis = largeChart.axisLeft
        leftAxis.textSize = 11f
        leftAxis.textColor = Color.DKGRAY
        leftAxis.mAxisMaximum = getLeftAxisMax()
        leftAxis.mAxisMinimum = getLeftAxisMin()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.valueFormatter = ChartYAxisValueFormatter()

        largeChart.axisRight.isEnabled = false
    }

    private fun getHistoryDataEntries(): ArrayList<Entry> {
        val data = FundsDataManager.dmfHistoryData
        var dataEntries: ArrayList<Entry> = ArrayList()
        for (idx in 0 until data.size) {
            val o = data[idx]
            val totalFundInvested = o.Capital!!.toFloat() * 1000000
            val gross = o.Gross!!.toFloat()
            val grossInvest = totalFundInvested + gross
            val fees = o.FeeT!!.toFloat()
            val netInvest = grossInvest - fees
            dataEntries.add(Entry(idx.toFloat(), netInvest))
        }
        return dataEntries
    }

    private fun getFeeDataEntries(): ArrayList<Entry> {
        val data = FundsDataManager.dmfHistoryData
        var dataEntries: ArrayList<Entry> = ArrayList()
        for (idx in 0 until data.size) {
            val o = data[idx]
            dataEntries.add(Entry(idx.toFloat(), o.FeeT!!.toFloat()))
        }

        return dataEntries
    }

    private fun getLeftAxisMax(): Float {
        val data = FundsDataManager.dmfHistoryData
        val o = data.first()
        val totalFundInvested = o.Capital!!.toFloat() * 1000000
        val gross = o.Gross!!.toFloat()
        val grossInvest = totalFundInvested + gross
        val fees = o.FeeT!!.toFloat()
        val netInvest = grossInvest - fees

        var maxed = netInvest
        for (i in 1 until data.size) {
            val obj = data[i]
            val oTotalFundInvested = obj.Capital!!.toFloat() * 1000000
            val oGross = obj.Gross!!.toFloat()
            val oGrossInvest = oTotalFundInvested + oGross
            val oFees = obj.FeeT!!.toFloat()
            val oNetInvest = oGrossInvest - oFees

            if (maxed <= oNetInvest) {
                maxed = oNetInvest
            }
        }

        return maxed
    }
    private fun getLeftAxisMin(): Float {
        val data = FundsDataManager.dmfHistoryData
        val o = data.first()
        val totalFundInvested = o.Capital!!.toFloat() * 1000000
        val gross = o.Gross!!.toFloat()
        val grossInvest = totalFundInvested + gross
        val fees = o.FeeT!!.toFloat()
        val netInvest = grossInvest - fees

        var mined = netInvest
        for (i in 1 until data.size) {
            val obj = data[i]
            val oTotalFundInvested = obj.Capital!!.toFloat() * 1000000
            val oGross = obj.Gross!!.toFloat()
            val oGrossInvest = oTotalFundInvested + oGross
            val oFees = obj.FeeT!!.toFloat()
            val oNetInvest = oGrossInvest - oFees

            if (mined >= oNetInvest) {
                mined = oNetInvest
            }
        }

        return mined
    }

    private fun getRightAxisMax(): Float {
        val data = FundsDataManager.dmfHistoryData
        var maxed = data.first().FeeT!!.toFloat()
        for (i in 1 until data.size) {
            val fee = data[i].FeeT!!.toFloat()
            if (maxed <= fee) {
                maxed = fee
            }
        }

        return maxed
    }
    private fun getRightAxisMin(): Float {
        val data = FundsDataManager.dmfHistoryData
        var mined = data.first().FeeT!!.toFloat()
        for (i in 1 until data.size) {
            val fee = data[i].FeeT!!.toFloat()
            if (mined >= fee) {
                mined = fee
            }
        }

        return mined
    }


    private fun setData() {
        val dataSetL: LineDataSet

        if (largeChart.data != null && largeChart.data.dataSetCount > 0) {
            dataSetL = largeChart.data.getDataSetByIndex(0) as LineDataSet
            dataSetL.values = getHistoryDataEntries()
            largeChart.data.notifyDataChanged()
            largeChart.notifyDataSetChanged()
        } else {
            // create a dataSet and give it a type
            dataSetL = LineDataSet(getHistoryDataEntries(), "Net Investment Value")
            dataSetL.axisDependency = YAxis.AxisDependency.LEFT
            dataSetL.color = Color.DKGRAY
            dataSetL.lineWidth = 2.0f
            dataSetL.setDrawCircles(false)

            val lineData = LineData(dataSetL)
            lineData.setDrawValues(false)

            //set data
            largeChart.data = lineData
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            (android.R.id.home) -> {
                this.finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }



    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {

        private val dateFormatShort = SimpleDateFormat("yyyyMMdd")

        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}