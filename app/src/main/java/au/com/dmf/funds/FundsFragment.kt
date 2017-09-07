package au.com.dmf.funds

import android.content.Context
import android.content.Intent
import android.icu.text.ScientificNumberFormatter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import au.com.dmf.InvitePeople.InvitePeopleActivity

import au.com.dmf.R
import au.com.dmf.data.FragmentToActivity
import au.com.dmf.notifications.NotificationsActivity

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FundsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FundsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FundsFragment : Fragment() {

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
            mListener!!.onFragmentInteraction(FragmentToActivity("Fund", Uri.EMPTY))
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_funds, container, false)

        var viewPagerAdapter = FundFragmentAdapter(childFragmentManager)
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                println("onPageScrollStateChanged : $state")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                println("onPageScrolled : $position")
            }

            override fun onPageSelected(position: Int) {
                //TODO: this is the to tell the target Fragment
                println("onPageSelected : $position")
            }
        })
        viewPager.adapter = viewPagerAdapter
        viewPagerAdapter.notifyDataSetChanged()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.titlebar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.action_notification) {
            val intent = Intent(activity, NotificationsActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.action_invite) {
            val intent = Intent(activity, InvitePeopleActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
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
        fun onFragmentInteraction(uri: FragmentToActivity)
    }

    interface OnFragmentUpdateTitileBarListener {
        //fun onFragmentUpdateTitleBar(title: String, )
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
         * @return A new instance of fragment FundsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FundsFragment {
            val fragment = FundsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
