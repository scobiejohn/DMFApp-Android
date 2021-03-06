package au.com.brightcapital.login

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.com.brightcapital.R
import au.com.brightcapital.utils.afterTextChanged
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        passwordResetSubmitButton.isEnabled = false
        passwordResetSubmitButton.setOnClickListener({
            exit(passwordResetTx.text.toString(), confirmationCodeTx.text.toString())
        })

        confirmationCodeTx.afterTextChanged {
            passwordResetSubmitButton.isEnabled = (confirmationCodeTx.text.toString().length > 5) && (passwordResetTx.text.toString().length > 5)
        }

        passwordResetTx.afterTextChanged {
            passwordResetSubmitButton.isEnabled = (confirmationCodeTx.text.toString().length > 5) && (passwordResetTx.text.toString().length > 5)
        }
    }

    override fun onBackPressed() {
        exit("", "")
    }

    private fun exit(newPass: String, code: String) {
        val intent = Intent()
        if (newPass == null || code == null) {
            intent.putExtra("newPassword", "")
            intent.putExtra("confirmCode", "")
        } else {
            intent.putExtra("newPassword", newPass)
            intent.putExtra("confirmCode", code)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
