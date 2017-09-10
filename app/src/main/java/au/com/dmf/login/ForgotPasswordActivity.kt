package au.com.dmf.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.com.dmf.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        passwordResetSubmitButton.isEnabled = false
    }
}
