package au.com.dmf.funds

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

import au.com.dmf.R
import au.com.dmf.data.FragmentToActivity
import au.com.dmf.login.PinCodeActivity
import au.com.dmf.model.User
import au.com.dmf.services.JiraServiceManager
import au.com.dmf.utils.alert
import com.afollestad.materialdialogs.MaterialDialog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.afollestad.materialdialogs.DialogAction
import com.vicpin.krealmextensions.queryFirst
import org.w3c.dom.Text
import au.com.dmf.R.id.passwordInput
import au.com.dmf.data.FundsDetail
import au.com.dmf.events.GetFundStateEvent
import work.wanghao.rxbus2.RxBus
import work.wanghao.rxbus2.Subscribe
import work.wanghao.rxbus2.ThreadMode


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DMFFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DMFFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DMFFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var smallChart: LineChart? = null
    private var fundSeekBar: SeekBar? = null
    private lateinit var fundSeekBarTitleTx: TextView
    private lateinit var cashPercentageTx: TextView
    private lateinit var fundPercentageTx: TextView

    private lateinit var strategyBtnTx: TextView
    private lateinit var transferBtnTx: TextView
    private lateinit var redeemBtnTx: TextView

    private lateinit var redeemAmountEditText: EditText
    private lateinit var transferAmountEditText: EditText

    private var seekBarProgress: Int = 0

    private var fundStrategy = "Aggressive"
    private var isStrategyChange = false
    private var isRedeemFund = false
    private var toStrategy = ""
    private var transferRedeemAMount = ""

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_dmf, container, false)

        fundSeekBarTitleTx = view.findViewById(R.id.fund_seek_bar_title)
        cashPercentageTx = view.findViewById(R.id.cash_percentage)
        fundPercentageTx = view.findViewById(R.id.fund_percentage)

        smallChart = view.findViewById(R.id.smallChart)
        smallChart!!.setOnLongClickListener({ _ ->
            val intent = Intent(activity, ChartActivity::class.java)
            activity.startActivity(intent)

            true
        })
        setData(10, 800f)

        this.fundSeekBar = view.findViewById(R.id.fund_seek_bar)
        this.fundSeekBar!!.incrementProgressBy(1)
        this.fundSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                cashFunChangeRequest(p0!!.progress)
            }
        })

        this.fundSeekBar!!.progress = seekBarProgress

        this.strategyBtnTx = view.findViewById(R.id.strategy_btn)
        this.transferBtnTx = view.findViewById(R.id.transfer_fund_btn)
        this.redeemBtnTx = view.findViewById(R.id.redeem_btn)

        this.strategyBtnTx.setOnClickListener({
            if (!hasPin()) {
                activity.alert("Reminder", "Please set up PIN first in 'Settings' page.")
            } else {

                MaterialDialog.Builder(activity)
                        .title("Multiplier")
                        .items(R.array.fundMultipliers)
                        .itemsCallbackSingleChoice(-1, { dialog, view, which, text ->

                            println(which)
                            println(text)

                            if (text != this.fundStrategy) {
                                this.toStrategy = text.toString()
                                this.isStrategyChange = true
                                this.showPinView()
                            }

                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             */
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             */
                            true
                        })
                        .positiveText("Choose")
                        .negativeColor(Color.GRAY)
                        .negativeText("Cancel")
                        .show()
            }
        })

        this.transferBtnTx.setOnClickListener({
            if (!hasPin()) {
                activity.alert("Reminder", "Please set up PIN first in 'Settings' page.")
            } else {
                val dialog = MaterialDialog.Builder(activity)
                        .title("Enter amount to transfer")
                        .customView(R.layout.dialog_transfer, true)
                        .positiveText("OK")
                        .negativeColor(Color.DKGRAY)
                        .negativeText(android.R.string.cancel)
                        .onPositive(
                                { dialog1, which ->
                                    println("Amount: " + transferAmountEditText.text.toString())
                                    this.transferRedeemAMount = transferAmountEditText.text.toString()
                                    this.isStrategyChange = false
                                    this.isRedeemFund = false
                                    this.showPinView()
                                })
                        .build()
                dialog.show()

                transferAmountEditText = dialog.customView!!.findViewById(R.id.transfer_amount_text)
            }
        })

        this.redeemBtnTx.setOnClickListener({
            if (!hasPin()) {
                activity.alert("Reminder", "Please set up PIN first in 'Settings' page.")
            } else {
                val dialog = MaterialDialog.Builder(activity)
                        .title("Enter amount to redeem")
                        .customView(R.layout.dialog_redeem, true)
                        .positiveText("OK")
                        .negativeColor(Color.DKGRAY)
                        .negativeText(android.R.string.cancel)
                        .onPositive(
                                { dialog1, which ->
                                    println("Amount: " + redeemAmountEditText.text.toString())
                                    this.transferRedeemAMount = redeemAmountEditText.text.toString()
                                    this.isStrategyChange = false
                                    this.isRedeemFund = true
                                    this.showPinView()
                                })
                        .build()
                dialog.show()

                redeemAmountEditText = dialog.customView!!.findViewById(R.id.redeem_amount_text)
            }
        })

        return view
    }

    private fun doStrategyChange() {
        JiraServiceManager.createTicket("Fund Multiplier", "Darling Macro Fund", null, null, this.toStrategy, null,
                {},
                {}
        )
    }

    private fun doRedeemFund() {
        val amount = "AUD" + transferRedeemAMount
        JiraServiceManager.createTicket("Redeem Funds", "Darling Macro Fund", null, amount, null, null,
                {},
                {}
        )
    }

    private fun doTransferFund() {
        val amount = "AUD" + transferRedeemAMount
        JiraServiceManager.createTicket("Transfer Funds", "Darling Macro Fund", null, amount, null, null,
                {},
                {}
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val pinResult = data!!.getStringExtra("PIN")
            if (pinResult == "YES") {
                when {
                    isStrategyChange -> {
                        doStrategyChange()
                    }
                    isRedeemFund -> {
                        doRedeemFund()
                    }
                    else -> {
                        doTransferFund()
                    }
                }
                isStrategyChange = false
                isRedeemFund = false
            }
        }
    }

    /**
     * Change Cash/Fund percentage
     */
    private fun cashFunChangeRequest(toProgress: Int) {

        if (toProgress == this.seekBarProgress) {
            return
        }

        val seekBarChangeString = "Cash " + displayValueForExtreme(toProgress, true) + "%, Fund " + displayValueForExtreme(5 - toProgress, false) + "%."
        val bodyContent = "Do you want to make a request for\n" + seekBarChangeString
        MaterialDialog.Builder(activity)
                .title("Cash Allocation Change")
                .content(bodyContent)
                .positiveText("OK")
                .negativeColor(Color.GRAY)
                .negativeText("Cancel")
                .onAny { _, which ->
                    if (which == DialogAction.POSITIVE) {
                        seekBarProgress = fundSeekBar!!.progress
                        cashPercentageTx.text = displayValueForExtreme(seekBarProgress, true) + "%"
                        fundPercentageTx.text = displayValueForExtreme(5 - seekBarProgress, false) + "%"
                        fundSeekBarTitleTx.text = "Funds In Cash : " + displayValueForExtreme(toProgress, true) + "%\nFunds In DMF : " + displayValueForExtreme(5 - toProgress, false) + "%"

                        JiraServiceManager.createTicket("Cash Allocation Change", "Darling Macro Fund", null, seekBarChangeString, null, null, {}, {})
                    } else {
                        fundSeekBar!!.progress = seekBarProgress

                    }
                }
                .show()
    }

    private fun showPinView() {
        val intent = Intent(activity, PinCodeActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun displayValueForExtreme(value: Int, forCash: Boolean = true): String {
        val updatedValue = 100 - value * 20
        var displayValue = updatedValue.toString()
        if (updatedValue == 0) {
            if (forCash) {
                displayValue = "Min"
            } else {
                displayValue = "0"
            }
        } else if (updatedValue == 100) {
            if (forCash) {
                displayValue = "Max"
            } else {
                displayValue = "100"
            }
        }

        return displayValue
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(fta: FragmentToActivity) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(fta)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onStart() {
        super.onStart()
        RxBus.Companion.get().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetFundStateEvent(evt: GetFundStateEvent) {
        val fundIno = FundsDetail.funds.get("Darling Macro Fund")
        if (fundIno != null) {
            println(fundIno.investable)
            transferBtnTx.isEnabled = fundIno.investable != "NO"
            transferBtnTx.alpha = if (fundIno.investable != "NO") 1.0f else 0.3f
        }
    }

    override fun onStop() {
        super.onStop()
        RxBus.Companion.get().unRegister(this)
    }

    private fun setData(count: Int, range: Float) {

        val values = ArrayList<Entry>()

        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() + 3
            values.add(Entry(i.toFloat(), `val`))
        }

        val set1: LineDataSet

        if (smallChart?.data != null && smallChart!!.data.dataSetCount > 0) {
            set1 = smallChart!!.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            smallChart!!.data.notifyDataChanged()
            smallChart!!.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")

            set1.setDrawIcons(false)

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(false)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

//            if (Utils.getSDKInt() >= 18) {
//                // fill drawable only supported on api level 18 and above
//                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
//                set1.fillDrawable = drawable
//            } else {
            set1.fillColor = Color.BLACK
//            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val data = LineData(dataSets)

            // set data
            smallChart!!.data = data
        }
    }

    private fun hasPin(): Boolean {
        val user = User().queryFirst()
        return user!!.pin != 0
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(fta: FragmentToActivity)
    }

    companion object {

        private val REQUEST_CODE = 4

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DMFFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): DMFFragment {
            val fragment = DMFFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
