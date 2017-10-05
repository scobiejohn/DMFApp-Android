package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import au.com.dmf.R
import au.com.dmf.utils.AWSManager
import au.com.dmf.utils.afterTextChanged

import kotlinx.android.synthetic.main.activity_new_password.*

class NewPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        newPasswordTx.afterTextChanged {
            newPasswordSubmitButton.isEnabled = newPasswordTx.text.toString().length > 5
        }

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
