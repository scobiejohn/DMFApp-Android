package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import au.com.dmf.R
import au.com.dmf.utils.AWSManager

import kotlinx.android.synthetic.main.activity_new_password.*

class NewPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        newPasswordTx.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                newPasswordSubmitButton.isEnabled = newPasswordTx.text.toString().length > 5
            }
        })

        newPasswordSubmitButton.isEnabled = false
        newPasswordSubmitButton.setOnClickListener({
            AWSManager.firstTimeLoginNewPassword = newPasswordTx.text.toString()
            exit(true)
        })


    }

    override fun onBackPressed() {
        exit(false)
    }

    private fun exit(continueWithSignIn: Boolean) {
        val intent = Intent()
        intent.putExtra("continueWithSignIn", continueWithSignIn)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
