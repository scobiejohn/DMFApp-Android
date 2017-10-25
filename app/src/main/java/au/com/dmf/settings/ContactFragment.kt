package au.com.dmf.settings


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import au.com.dmf.R
import au.com.dmf.model.User
import au.com.dmf.services.DynamoDBManager
import au.com.dmf.utils.Util
import au.com.dmf.utils.afterTextChanged
import au.com.dmf.utils.hideSoftKeyBoard
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
import com.amazonaws.services.simpleemail.model.*
import com.vicpin.krealmextensions.queryFirst
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * A simple [Fragment] subclass.
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : Fragment() {

    private lateinit var subjectTxt: EditText
    private lateinit var bodyTxt: EditText
    private lateinit var sendButton: Button

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_contact, container, false)
        subjectTxt = view.findViewById(R.id.contact_subject)
        bodyTxt = view.findViewById(R.id.contact_body)
        sendButton = view.findViewById(R.id.contact_send_button)

        subjectTxt.afterTextChanged {
            sendButton.isEnabled = subjectTxt.text.toString().trim().isNotEmpty() && bodyTxt.text.toString().trim().isNotEmpty()
        }
        bodyTxt.afterTextChanged {
            sendButton.isEnabled = subjectTxt.text.toString().trim().isNotEmpty() && bodyTxt.text.toString().trim().isNotEmpty()
        }
        sendButton.setOnClickListener({
            val user = User().queryFirst()

            val ses = AmazonSimpleEmailServiceClient(DynamoDBManager.credentials)
            ses.setRegion(Region.getRegion(Regions.US_EAST_1))
            val subject = Content(subjectTxt.text.toString().trim())
            val body = Body(Content(bodyTxt.text.toString().trim()))
            val message = Message(subject, body)
            val to = "contact@darlingmacro.fund"
            val destination = Destination()
                    .withToAddresses(arrayListOf(to))

            doAsync {
                try {
                    val request = SendEmailRequest(user!!.email, destination, message)
                    ses.sendEmail(request)
                    uiThread {
                        hideSoftKeyBoard(activity)
                        subjectTxt.setText("")
                        bodyTxt.setText("")
                        Toast.makeText(activity, "Email sent.",
                                Toast.LENGTH_SHORT).show()
                    }
                } catch (err: Exception) {
                    uiThread {
                        Toast.makeText(activity, "Email couldn't be sent.",
                                Toast.LENGTH_SHORT).show()
                    }
                }

            }

        })

        return view
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
         * @return A new instance of fragment ContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ContactFragment {
            val fragment = ContactFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
