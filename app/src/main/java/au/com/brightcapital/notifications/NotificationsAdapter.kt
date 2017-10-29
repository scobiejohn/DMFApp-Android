package au.com.brightcapital.notifications

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import au.com.brightcapital.R
import au.com.brightcapital.services.DynamoDBManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by raymond on 12/10/17.
 */
class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.NotifViewHolder>(){

    private var notifs: ArrayList<DynamoDBManager.DMFNotificationTableRow> = ArrayList()
    private val timeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun updateData(data: ArrayList<DynamoDBManager.DMFNotificationTableRow>) {
        notifs.clear()
        notifs.addAll(data)
        notifyDataSetChanged()
    }

    inner class NotifViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val messageTextView = view.findViewById<TextView>(R.id.notification_body)
        private val timestampTextView = view.findViewById<TextView>(R.id.notification_timestamp)
        fun bindItem(notification: DynamoDBManager.DMFNotificationTableRow) {
            messageTextView.text = notification.Message
            timestampTextView.text = timeFormatter.format(Date(notification.CreatedAt!!))
        }
    }

    override fun onBindViewHolder(holder: NotifViewHolder?, position: Int) {
        val notif = notifs[position]
        holder?.bindItem(notif)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NotifViewHolder {
        val messageView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.notification_list_row, parent, false)
        return NotifViewHolder(messageView)
    }

    override fun getItemCount(): Int {
        return notifs.size
    }


}