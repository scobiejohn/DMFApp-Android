package au.com.brightcapital.tasks

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import au.com.brightcapital.R
import au.com.brightcapital.data.Task
import au.com.brightcapital.utils.Constants
import java.text.SimpleDateFormat

class TasksAdapter(private val listener: OnTaskClickListener) : RecyclerView.Adapter<TasksAdapter.MyViewHolder>() {

    interface OnTaskClickListener {
        fun onTaskClick(task: Task)
    }

    var tasks: ArrayList<Task> = ArrayList()
    var numTotalTasks = 0

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
    private var context: Context? = null

    fun updateData(data: ArrayList<Task>) {
        tasks.clear()
        tasks.addAll(data)
        notifyDataSetChanged()
    }

    fun appendData(data: ArrayList<Task>) {
        val size = tasks.size
        tasks.addAll(data)
        notifyItemInserted(size)
    }

    fun updateSingleData(task: Task) {
        val idx = tasks.indexOf(task)
        task.status = Constants.TASK_STATUS_WITHDRAWN
        notifyItemChanged(idx)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var taskStatus = view.findViewById<View>(R.id.taskStatus) as TextView
        var taskDescription = view.findViewById<View>(R.id.taskDescription) as TextView
        var withdrawnText = view.findViewById<View>(R.id.withdrawnText) as TextView
        var taskDot = view.findViewById<View>(R.id.taskDot) as View
        var taskDate = view.findViewById<View>(R.id.taskDate) as TextView
        var taskStatusIconImage = view.findViewById<View>(R.id.taskIconImage) as View

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.task_list_row, parent, false)

        this.context = parent.context
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskStatus.text = task.status
        holder.taskDescription.text = task.details
        holder.taskDate.text = dateFormatter.format(task.timeStamp)

        when (task.status) {
            Constants.TASK_STATUS_OPEN -> {
                holder.withdrawnText.visibility = View.VISIBLE
                holder.taskDot.background.clearColorFilter()
                holder.taskDot.background.setColorFilter(context!!.resources.getColor(R.color.colorBlueGrey), PorterDuff.Mode.MULTIPLY)
                holder.withdrawnText.setOnClickListener{ _ ->
                    listener.onTaskClick(tasks[position])
                }
            }
            Constants.TASK_STATUS_INPROGRESS -> {
                holder.withdrawnText.visibility = View.GONE

                holder.taskDot.background.clearColorFilter()
                holder.taskDot.background.setColorFilter(context!!.resources.getColor(R.color.colorYellow), PorterDuff.Mode.MULTIPLY)
            }
            Constants.TASK_STATUS_CLOSED,
            Constants.TASK_STATUS_DONE,
            Constants.TASK_STATUS_WITHDRAWN,
            Constants.TASK_STATUS_RESOLVED -> {
                holder.withdrawnText.visibility = View.GONE

                holder.taskDot.background.clearColorFilter()
                holder.taskDot.background.setColorFilter(context!!.resources.getColor(R.color.colorGreen), PorterDuff.Mode.MULTIPLY)

                holder.taskStatusIconImage.setBackgroundResource(R.drawable.ic_done_black_24dp)
                holder.taskStatusIconImage.background.clearColorFilter()
                holder.taskStatusIconImage.background.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY)
            }
            else -> {
                holder.withdrawnText.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

}
