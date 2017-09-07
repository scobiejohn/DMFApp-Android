package au.com.dmf.settings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient

import au.com.dmf.R
import au.com.dmf.data.FragmentToActivity

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HtmlFileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HtmlFileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HtmlFileFragment : Fragment() {

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
            val titleName = if (mParam1?.indexOf("user-guide") == -1) {
                "T&Cs"
            } else {
                "User Guide"
            }
            mListener!!.onFragmentInteraction(FragmentToActivity(titleName, Uri.EMPTY))
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater!!.inflate(R.layout.fragment_html_file, container, false)
        val webView = view.findViewById<WebView>(R.id.webView)
        val htmlProgressBar = view.findViewById<View>(R.id.htmlProgressBar)

        webView.webViewClient = (object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                if (htmlProgressBar != null) {
                    htmlProgressBar.visibility = View.GONE
                }
            }
        })
        webView.loadUrl(mParam1)

        return view
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
        if (mListener != null) {
            mListener!!.onFragmentInteraction(FragmentToActivity("Settings", Uri.EMPTY))
        }

        super.onDetach()
        mListener = null
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
         * @return A new instance of fragment HtmlFileFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): HtmlFileFragment {
            val fragment = HtmlFileFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
