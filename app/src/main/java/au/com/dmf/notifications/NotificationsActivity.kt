package au.com.dmf.notifications

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.Window
import au.com.dmf.R

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        this.setFinishOnTouchOutside(false)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setLayout(width, height)

    }
}
