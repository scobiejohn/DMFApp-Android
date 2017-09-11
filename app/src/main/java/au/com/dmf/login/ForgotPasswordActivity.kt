package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import au.com.dmf.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        passwordResetSubmitButton.isEnabled = false
        passwordResetSubmitButton.setOnClickListener({
            exit(confirmationCodeTx.text.toString(), passwordResetTx.text.toString())
        })

        confirmationCodeTx.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                passwordResetSubmitButton.isEnabled = (confirmationCodeTx.text.toString().length > 5) && (passwordResetTx.text.toString().length > 5)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun equals(other: Any?): Boolean {
                return super.equals(other)
            }
            override fun hashCode(): Int {
                return super.hashCode()
            }
            override fun toString(): String {
                return super.toString()
            }
        })
        passwordResetTx.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                passwordResetSubmitButton.isEnabled = (confirmationCodeTx.text.toString().length > 5) && (passwordResetTx.text.toString().length > 5)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun equals(other: Any?): Boolean {
                return super.equals(other)
            }
            override fun hashCode(): Int {
                return super.hashCode()
            }
            override fun toString(): String {
                return super.toString()
            }
        })
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
