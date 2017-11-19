package au.com.brightcapital.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import au.com.brightcapital.LaunchActivity

import au.com.brightcapital.R
import au.com.brightcapital.data.FragmentToActivity
import au.com.brightcapital.model.User
import au.com.brightcapital.services.DynamoDBManager
import au.com.brightcapital.utils.AWSManager
import com.afollestad.materialdialogs.MaterialDialog
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.fragment_settings.*
import android.graphics.Color
import au.com.brightcapital.data.FundInfo
import au.com.brightcapital.data.FundsDetail
import au.com.brightcapital.utils.Constants
import au.com.brightcapital.utils.afterTextChanged
import au.com.brightcapital.utils.hideSoftKeyBoard
import org.jetbrains.anko.toast


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), HtmlFileFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(fta: FragmentToActivity) {

    }

    private lateinit var checkBox: CheckBox
    private lateinit var pinET: EditText
    private lateinit var savePinButton: Button
    private lateinit var signOutSessionTV: TextView
    private lateinit var reportingPeriodTV: TextView

    private val autoSignOutOptions: ArrayList<String> =  ArrayList()
    private val reportingPeriodOptions: ArrayList<String> = ArrayList()

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }


        if (mListener != null) {
            mListener!!.onFragmentInteraction(FragmentToActivity("Settings", Uri.EMPTY))
        }

        val options = arrayOf("Never", "After 5 Mins", "After 15 Mins", "After 30 Mins", "After 60 Mins")
        autoSignOutOptions.addAll(options)
        val periodOptions = arrayOf(Constants.ReportingOneMonth, Constants.ReportingThreeMonths, Constants.ReportingJuneToDate, Constants.ReportingYearToDate,
                Constants.ReportingTwelveMonths, Constants.ReportingThirtySixMonths, Constants.ReportingSixtyMonths, Constants.ReportingHundredTwentyMonths)
        reportingPeriodOptions.addAll(periodOptions)
    }

    private val onOpenDocView = View.OnClickListener { view ->
        var filePath = if (view == legalButton) {
            "https://s3-ap-southeast-2.amazonaws.com/dmf-app/doc/terms-and-condition-android.html"
        } else {
            "https://s3-ap-southeast-2.amazonaws.com/dmf-app/doc/user-guide-android.html"
        }

        val transaction = activity.supportFragmentManager.beginTransaction()
        val fragment = HtmlFileFragment.newInstance(filePath, "")
        transaction.add(R.id.content, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.addToBackStack("HtmlFileFragment")
        transaction.commit()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)

        val legalButton = view.findViewById<Button>(R.id.legalButton)
        legalButton.setOnClickListener(onOpenDocView)
        val userGuideButton = view.findViewById<Button>(R.id.userGuideButton)
        userGuideButton.setOnClickListener(onOpenDocView)

        val refreshDataButton = view.findViewById<Button>(R.id.refreshDataButton)
        refreshDataButton.setOnClickListener {
            DynamoDBManager.getFundDetails("Darling Macro Fund", {row ->
                val fundInfo = FundInfo(row.InMarket!!, row.Investable!!)
                FundsDetail.funds.put("Darling Macro Fund", fundInfo)
                activity.toast("The latest data has been checked.")
            }, {})
        }

        val signOutButton = view.findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener({
            AWSManager.userPool?.currentUser?.signOut()
            val intent = Intent(activity.applicationContext, LaunchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        })

        checkBox = view.findViewById<CheckBox>(R.id.pin_check_box)
        pinET = view.findViewById(R.id.pin_code)
        savePinButton = view.findViewById(R.id.save_pin_button)
        checkBox.setOnCheckedChangeListener{_, isChecked ->
            pinET.visibility = if(isChecked) View.VISIBLE else View.GONE
            savePinButton.visibility = if(isChecked) View.VISIBLE else View.GONE
            if (pinET.visibility == View.GONE) {
                pinET.setText("")

                val user = User().queryFirst()
                user!!.pin = 0
                user!!.save()
            }
        }
        pinET.afterTextChanged {
            savePinButton.isEnabled = it.length >= 4
        }
        savePinButton.setOnClickListener {
            //update pin
            val user = User().queryFirst()
            if (checkBox.isChecked && pinET.text.length == 4) {
                user!!.pin = pinET.text.toString().toInt()
                user!!.save()
            }

            try {
                hideSoftKeyBoard(activity)
            }catch (err: Exception){}
        }

        val user = User().queryFirst()
        if (user!!.pin != 0) {
            checkBox.isChecked = true
            pinET.setText(user!!.pin.toString())
        }

        val userEmailTx = view.findViewById<TextView>(R.id.user_email_tx)
        userEmailTx.text = user!!.name + "  " + user!!.email

        val autoSignOutButton = view.findViewById<Button>(R.id.auto_sign_out_session_button)
        signOutSessionTV = view.findViewById<TextView>(R.id.auto_sign_out_session_code)
        signOutSessionTV.text = autoSignOutSessionValueToLabel(user!!.sessionDuration)
        autoSignOutButton.setOnClickListener({
            openSignOutSessionDialog(user!!.sessionDuration)
        })

        val reportingPeriodButton = view.findViewById<Button>(R.id.price_date_period_button)
        reportingPeriodTV = view.findViewById<TextView>(R.id.price_data_period_label)
        reportingPeriodTV.text = user!!.reportingPeriod
        reportingPeriodButton.setOnClickListener({
            openReportingPeriodDialog(user!!.reportingPeriod)
        })

        val contactButton = view.findViewById<Button>(R.id.contactButton)
        contactButton.setOnClickListener {
            val transaction = activity.supportFragmentManager.beginTransaction()
            val fragment = ContactFragment.newInstance("", "")
            transaction.add(R.id.content, fragment)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.addToBackStack("ContactFragment")
            transaction.commit()
        }

        return view
    }

    private fun openReportingPeriodDialog(reportingPeriod: String) {
        val selectedIndex = when(reportingPeriod) {
            Constants.ReportingOneMonth -> 0
            Constants.ReportingThreeMonths -> 1
            Constants.ReportingJuneToDate -> 2
            Constants.ReportingYearToDate -> 3
            Constants.ReportingTwelveMonths -> 4
            Constants.ReportingThirtySixMonths -> 5
            Constants.ReportingSixtyMonths -> 7
            else -> 8
        }

        MaterialDialog.Builder(activity)
                .title("Choose Reporting Period")
                .items(reportingPeriodOptions)
                .itemsCallbackSingleChoice(selectedIndex, MaterialDialog.ListCallbackSingleChoice { dialog, view, which, text ->
                    updateUserReportingPeriod(which)
                    true
                })
                .negativeText("Cancel")
                //.negativeColor(ContextCompat.getColor(activity.applicationContext, R.color.dark_grey_color))
                .negativeColor(Color.GRAY)
                .positiveText("Choose")
                .cancelable(false)
                .show()
    }

    private fun updateUserReportingPeriod(periodIndex: Int) {
        val selectedReportingPeriod = when(periodIndex) {
            0 -> Constants.ReportingOneMonth
            1 -> Constants.ReportingThreeMonths
            2 -> Constants.ReportingJuneToDate
            3 -> Constants.ReportingYearToDate
            4 -> Constants.ReportingTwelveMonths
            5 -> Constants.ReportingThreeMonths
            6 -> Constants.ReportingSixtyMonths
            else -> Constants.ReportingHundredTwentyMonths
        }
        val user = User().queryFirst()
        if (user?.reportingPeriod != selectedReportingPeriod) {
            user?.reportingPeriod = selectedReportingPeriod
            user?.save()
            reportingPeriodTV.text = selectedReportingPeriod
        }
    }

    private fun openSignOutSessionDialog(sessionDuration: Int) {
        val selectedIndex = when(sessionDuration) {
            5 -> 1
            15 -> 2
            30 -> 3
            60 -> 4
            else -> 0
        }

        MaterialDialog.Builder(activity)
                .title("Auto Sign Out")
                .items(autoSignOutOptions)
                .itemsCallbackSingleChoice(selectedIndex, MaterialDialog.ListCallbackSingleChoice { dialog, view, which, text ->
                    /**
                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected radio button to actually be selected.
                     */
                    /**
                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected radio button to actually be selected.
                     */
                    updateUserSessionDuration(which)
                    true
                })
                .negativeText("Cancel")
                //.negativeColor(ContextCompat.getColor(activity.applicationContext, R.color.dark_grey_color))
                .negativeColor(Color.GRAY)
                .positiveText("Choose")
                .cancelable(false)
                .show()
    }

    private fun updateUserSessionDuration(sessionDurationIndex: Int) {
        val updatedSessionDuration = when(sessionDurationIndex) {
            1 -> 5
            2 -> 15
            3 -> 30
            4 -> 60
            else -> 0
        }

        val user = User().queryFirst()
        user!!.sessionDuration = updatedSessionDuration
        user!!.save()

        signOutSessionTV.text = autoSignOutSessionValueToLabel(updatedSessionDuration)
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


    /**
     * helper
     */
    private fun autoSignOutSessionLabelToValue(label: String): Int {
        return when(label) {
            autoSignOutOptions[1] -> 5
            autoSignOutOptions[2] -> 15
            autoSignOutOptions[3] -> 30
            autoSignOutOptions[4] -> 60
            else -> 0
        }
    }
    private fun autoSignOutSessionValueToLabel(value: Int): String {
        return when(value) {
            5 -> autoSignOutOptions[1]
            15 -> autoSignOutOptions[2]
            30 -> autoSignOutOptions[3]
            60 -> autoSignOutOptions[4]
            else -> autoSignOutOptions[0]
        }
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
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
