package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.com.dmf.R
import au.com.dmf.utils.afterterTextChanged
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        passwordResetSubmitButton.isEnabled = false
        passwordResetSubmitButton.setOnClickListener({
            exit(confirmationCodeTx.text.toString(), passwordResetTx.text.toString())
        })

        confirmationCodeTx.afterterTextChanged {
            passwordResetSubmitButton.isEnabled = (confirmationCodeTx.text.toString().length > 5) && (passwordResetTx.text.toString().length > 5)
        }

        passwordResetTx.afterterTextChanged {
            passwordResetSubmitButton.isEnabled = (confirmationCodeTx.text.toString().length > 5) && (passwordResetTx.text.toString().length > 5)
        }
    }

    private fun exit(newPass: String, code: String) {
        val intent = Intent()
        if (newPass == null || code == null) {
            intent.putExtra("newPass", "")
            intent.putExtra("code", "")
        } else {
            intent.putExtra("newPass", newPass)
            intent.putExtra("code", code)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
