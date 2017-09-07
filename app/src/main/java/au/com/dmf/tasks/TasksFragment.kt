package au.com.dmf.tasks

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import au.com.dmf.R
import au.com.dmf.data.FragmentToActivity
import au.com.dmf.data.Task
import au.com.dmf.services.JiraServiceManager
import com.beust.klaxon.JsonObject
import com.beust.klaxon.obj
import com.beust.klaxon.string
import kotlinx.android.synthetic.main.fragment_tasks.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TasksFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TasksFragment : Fragment() {

    private var tasksListView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: TasksAdapter? = null
    private var taskVerticalLine: View? = null

    private var tasks: ArrayList<Task>? = ArrayList<Task>()

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
            mListener!!.onFragmentInteraction(FragmentToActivity("Tasks", Uri.EMPTY))
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_tasks, container, false)

        var localTasksListView = view.findViewById<RecyclerView>(R.id.tasksListView)
        localTasksListView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        adapter = TasksAdapter(tasks!!)
        layoutManager = LinearLayoutManager(activity.applicationContext)
        localTasksListView.layoutManager = layoutManager
        localTasksListView.itemAnimator = DefaultItemAnimator()
        localTasksListView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(localTasksListView.context, LinearLayoutManager.VERTICAL)
        localTasksListView.addItemDecoration(dividerItemDecoration)

        this.tasksListView = localTasksListView

        this.taskVerticalLine = view.findViewById(R.id.taskVerticalLine)

        prepareTaskData()

        return view
    }

    private fun prepareTaskData() {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        JiraServiceManager.getTasks(0, { (jsonArray) ->
            for (i in 0 until jsonArray.size) {
                val task = Task()
                task.id = jsonArray[i].string("id")!!
                val o: JsonObject = (jsonArray[i]).obj("fields") as JsonObject
                task.details = o.string("summary")!!
                task.status = o.obj("status")?.string("name")!!
                val timeString = o.string("created")
                task.timeStamp = formatter.parse(timeString)
                tasks!!.add(task)
            }

            if (taskListProgressBar != null) {
                taskListProgressBar.visibility = View.GONE
            }
            adapter?.notifyDataSetChanged()
            this.taskVerticalLine!!.visibility = View.VISIBLE
        })
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
         * @return A new instance of fragment TasksFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): TasksFragment {
            val fragment = TasksFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
