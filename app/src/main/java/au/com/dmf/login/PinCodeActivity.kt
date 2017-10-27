package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import au.com.dmf.R
import au.com.dmf.model.User
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.vicpin.krealmextensions.queryFirst

class PinCodeActivity : AppCompatActivity() {

    private lateinit var indicatorDots: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        val user = User().queryFirst()

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window.setBackgroundDrawable(null)

        indicatorDots = findViewById<IndicatorDots>(R.id.indicator_dots)
        val pinCodeView = findViewById<PinLockView>(R.id.pin_lock_view)
        pinCodeView.pinLength = 4
        pinCodeView.attachIndicatorDots(indicatorDots as IndicatorDots?)
        val self = this
        pinCodeView.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String?) {
                println(pin)
                if (pin == user!!.pin.toString()) {
                    val intent = Intent()
                    intent.putExtra("PIN", "YES")
                    setResult(Activity.RESULT_OK, intent)
                    self.finish()
                } else {
                    errorShake()
                }
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {

            }

            override fun onEmpty() {

            }
        })

        val cancelBtn = findViewById<TextView>(R.id.pin_code_cancel_btn)
        cancelBtn.setOnClickListener({
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            this.finish()
        })
    }

    private fun errorShake() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.shake)
        indicatorDots.startAnimation(anim)
    }

}
