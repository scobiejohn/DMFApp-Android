package au.com.dmf.notifications

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import au.com.dmf.R

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        supportActionBar?.title = Html.fromHtml("<font color='#ffffff'>Notifications</font>")
    }
}
