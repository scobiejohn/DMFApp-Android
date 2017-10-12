package au.com.dmf.notifications

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import au.com.dmf.R
import au.com.dmf.services.DynamoDBManager

class NotificationsActivity : AppCompatActivity() {

    private lateinit var notifsRecyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        this.setFinishOnTouchOutside(false)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setLayout(width, height)

        notifsRecyclerView = findViewById(R.id.notifsRecyclerView)
        notifsRecyclerView.layoutManager = LinearLayoutManager(this)
        notifsRecyclerView.itemAnimator = DefaultItemAnimator()
        adapter = NotificationsAdapter()
        notifsRecyclerView.adapter = adapter

        loadNotificationData()
    }

    private fun loadNotificationData() {
        DynamoDBManager.getNotifications(50, {data ->
            adapter.updateData(data)
        }, {})
    }
}
