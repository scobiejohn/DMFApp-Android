package au.com.dmf.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.com.dmf.R
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView

class PinCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        val indicatorDots = findViewById<IndicatorDots>(R.id.indicator_dots)
        val pinCodeView = findViewById<PinLockView>(R.id.pin_lock_view)
        pinCodeView.pinLength = 4
        pinCodeView.attachIndicatorDots(indicatorDots)
        pinCodeView.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String?) {
                println(pin)
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {

            }

            override fun onEmpty() {

            }
        })
    }
}
