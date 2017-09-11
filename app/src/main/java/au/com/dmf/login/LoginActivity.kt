package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import au.com.dmf.MainActivity
import au.com.dmf.R
import au.com.dmf.model.User
import au.com.dmf.utils.AWSManager
import com.afollestad.materialdialogs.MaterialDialog
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.vicpin.krealmextensions.queryFirst

import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity() {

    lateinit var waitDialog: MaterialDialog
    lateinit var resetPasswordContinuation: ForgotPasswordContinuation
    lateinit var newPasswordContinuation: NewPasswordContinuation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitBtn.isEnabled = false
        resetPasswordButton.isEnabled = false

        userNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                submitBtn.isEnabled = (userNameInput.text.toString().length > 1 && passwordInput.text.toString().length > 5)
                resetPasswordButton.isEnabled = userNameInput.text.toString().length > 1
            }
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                submitBtn.isEnabled = (userNameInput.text.toString().length > 2 && passwordInput.text.toString().length > 5)
            }
        })

        submitBtn.setOnClickListener({
            //User("1234", "6789", 1111, "ray@mail.com", "Raymond").save()

            signIn()
        })

        resetPasswordButton.setOnClickListener({
            resetPassword()

            /*
            MaterialDialog.Builder(this)
                    .title("Password successfully changed.")
                    .content("")
                    .positiveText("Close")
                    .show()
                    */
        })

        /*
        resetPasswordButton.setOnClickListener({
            val firstUser = User().queryFirst()
            print(firstUser)
        })
        */

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            //new password for first sign in
            (1) -> {
                if (resultCode == Activity.RESULT_OK) {
                    val continueSignIn = data.getBooleanExtra("continueSignIn", false)
                    if (continueSignIn) {
                        continueWithFirstTimeSignIn()
                    }
                }
            }
            //reset password
            (2) -> {
                if (resultCode == Activity.RESULT_OK) {
                    val newPassword = data.getStringExtra("newPassword")
                    val confirmCode = data.getStringExtra("confirmCode")
                    if (newPassword != null && confirmCode != null) {
                        showWaitDialog("Setting new password")
                        resetPasswordContinuation.setPassword(newPassword)
                        resetPasswordContinuation.setVerificationCode(confirmCode)
                        resetPasswordContinuation.continueTask()
                    }
                }
            }
        }
    }

    private fun continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AWSManager.firstTimeLoginNewPassword)
        try {
            newPasswordContinuation.continueTask()
        }catch (ex: Exception){
            closeWaitWaitDialog()
            showWaitDialog("Sign in failed", AWSManager.formatException(ex))
        }
    }

    private fun signIn() {
        val userName = userNameInput.text.toString()
        AWSManager.user = userName
        showWaitDialog("Sign in")

        AWSManager.userPool?.getUser(userName)?.getSessionInBackground(authenticationHandler)
    }

    fun getUserAuthentication(continuation: AuthenticationContinuation, userName: String) {
        val authenticationDetails = AuthenticationDetails(userNameInput.text.toString(), passwordInput.text.toString(), null)
        continuation.setAuthenticationDetails(authenticationDetails)
        continuation.continueTask()
    }

    private fun showWaitDialog(title: String, body: String = "please wait ...") {
        closeWaitWaitDialog()
        waitDialog = MaterialDialog.Builder(this)
                .autoDismiss(false)
                .title(title)
                .content(body)
                .progress(true, 0)
                .show()
        waitDialog.setCancelable(false)
    }
    private fun closeWaitWaitDialog() {
        try {
            waitDialog.dismiss()
        } catch (err: Exception){}
    }

    private fun clearInputs() {
        userNameInput.setText("")
        passwordInput.setText("")
    }

    private fun enterApp() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    private fun firstTimeSignIn() {
        val intent = Intent(this, NewPasswordActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun resetPassword() {
        AWSManager.userPool?.getUser(userNameInput.text.toString())?.forgotPasswordInBackground(forgotPasswordHandler)
    }

    private fun getResetPassword(continuation: ForgotPasswordContinuation?) {
        this.resetPasswordContinuation = continuation!!
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivityForResult(intent, 2)
    }

    private val authenticationHandler = (object: AuthenticationHandler {
        override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            println("authenticationChallenge")

            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED" == continuation?.challengeName) {
                newPasswordContinuation = continuation as NewPasswordContinuation
                closeWaitWaitDialog()
                firstTimeSignIn()
            }
        }

        override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
            println("getAuthenticationDetails")
            getUserAuthentication(authenticationContinuation!!, userId!!)
        }

        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
            println("getMFACode")
        }

        override fun onFailure(exception: java.lang.Exception?) {
            println("onFailure")

            clearInputs()
            closeWaitWaitDialog()
        }

        override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
            println("onSuccess")

            closeWaitWaitDialog()

            enterApp()
        }
    })

    private val forgotPasswordHandler = (object: ForgotPasswordHandler {
        override fun onSuccess() {
            closeWaitWaitDialog()
            MaterialDialog.Builder(applicationContext)
                    .title("Password successfully changed.")
                    .content("")
                    .positiveText("Close")
                    .show()
        }

        override fun onFailure(exception: java.lang.Exception?) {
            closeWaitWaitDialog()
            MaterialDialog.Builder(applicationContext)
                    .title("Reset password failed.")
                    .content("")
                    .positiveText("Close")
                    .show()
        }

        override fun getResetCode(continuation: ForgotPasswordContinuation?) {
            closeWaitWaitDialog()
            getResetPassword(continuation)
        }

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


