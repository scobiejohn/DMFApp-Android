package au.com.dmf.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.WindowCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import au.com.dmf.MainActivity
import au.com.dmf.R
import au.com.dmf.model.User
import au.com.dmf.utils.AWSManager
import au.com.dmf.utils.Constants
import au.com.dmf.utils.afterTextChanged
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.awt.font.TextAttribute
import java.util.*

class LoginActivity : AppCompatActivity() {

    lateinit var userName: String
    lateinit var password: String

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

    lateinit var waitDialog: MaterialDialog
    lateinit var resetPasswordContinuation: ForgotPasswordContinuation
    lateinit var newPasswordContinuation: NewPasswordContinuation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitBtn.isEnabled = false
        resetPasswordButton.isEnabled = false

        userNameInput.afterTextChanged {
            userName = userNameInput.text.toString()
            password = passwordInput.text.toString()
            submitBtn.isEnabled = userName.length > 1 && password.length > 5
            resetPasswordButton.isEnabled = userNameInput.text.toString().length > 1
        }

        passwordInput.afterTextChanged {
            userName = userNameInput.text.toString()
            password = passwordInput.text.toString()
            submitBtn.isEnabled = userName.length > 1 && password.length > 5
        }

        submitBtn.setOnClickListener({
            //User("1234", "6789", 1111, "ray@mail.com", "Raymond").save()
            signIn()
        })

        resetPasswordButton.setOnClickListener({
            resetPassword()
        })

        val user = User().queryFirst()
        if (user!!.pin != 0) {
            val intent = Intent(this, PinCodeActivity::class.java)
            startActivityForResult(intent, 3)
        }

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
            //pin pad
            (3) -> {
                if (resultCode == Activity.RESULT_OK) {
                    val pinResult = data!!.getStringExtra("PIN")
                    if (pinResult == "YES") {
                        signInPanel.visibility = View.INVISIBLE
                        resetPasswordButton.visibility = View.INVISIBLE
                        val user = User().queryFirst()
                        userName = user!!.name
                        password = user!!.password
                        signIn()
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
        AWSManager.user = userName
        showWaitDialog("Sign in")

        AWSManager.userPool?.getUser(userName)?.getSessionInBackground(authenticationHandler)
    }

    fun getUserAuthentication(continuation: AuthenticationContinuation, userName: String) {
        val authenticationDetails = AuthenticationDetails(userName, password, null)
        continuation.setAuthenticationDetails(authenticationDetails)
        continuation.continueTask()
    }

    private fun showWaitDialog(title: String, body: String = "please wait ...") {
        closeWaitWaitDialog()
        waitDialog = MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .autoDismiss(false)
                .customView(R.layout.sign_in_dialog, false)
                .show()
        waitDialog.setCancelable(false)
        waitDialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
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

    private fun getUserDetails(loginKey: String, loginValue: String) {
        val userName = AWSManager.user
        AWSManager.userPool?.getUser(userName)?.getDetailsInBackground(object : GetDetailsHandler{
            override fun onSuccess(cognitoUserDetails: CognitoUserDetails?) {
                println("$cognitoUserDetails")
                enterApp(cognitoUserDetails!!, loginKey, loginValue)
            }

            override fun onFailure(exception: java.lang.Exception?) {
                println("Could not fetch user details")
            }
        })

    }

    private fun enterApp(userDetails:CognitoUserDetails, loginKey: String, loginValue: String) {
        val user = User().queryFirst()

        var email = ""
        var fileName = ""
        for ((key, value) in userDetails.attributes.attributes) {
            if (key == "email") {
                email = value
            } else if (key == "custom:fund_file_name") {
                fileName = value
            }
        }

        if (user != null) {
            user!!.name = userName
            user!!.password = password
            user!!.email = email
            user!!.fundFile = fileName
            user!!.save()
        } else {
            User(userName, password, 0, email, fileName, 0, -1, "19700101").save()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("loginKey", loginKey)
        intent.putExtra("loginValue", loginValue)
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

            signInPanel.visibility = View.VISIBLE
            resetPasswordButton.visibility = View.VISIBLE
            clearInputs()
            closeWaitWaitDialog()
        }

        override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
            println("onSuccess")

            closeWaitWaitDialog()
            val idToken = userSession?.idToken?.jwtToken
            println("idToken : $idToken")
            val keyString = "cognito-idp.ap-southeast-2.amazonaws.com/" + Constants.CognitoIdentityUserPoolId

            getUserDetails(keyString, idToken!!)
        }
    })

    private val forgotPasswordHandler = (object: ForgotPasswordHandler {
        override fun onSuccess() {
            closeWaitWaitDialog()
            MaterialDialog.Builder(this@LoginActivity)
                    .title("Password successfully changed.")
                    .content("")
                    .positiveText("Close")
                    .show()
        }

        override fun onFailure(exception: java.lang.Exception?) {
            println(exception.toString())
            closeWaitWaitDialog()
            MaterialDialog.Builder(this@LoginActivity)
                    .title("Reset password failed.")
                    .content("")
                    .positiveText("Close")
                    .show()
        }

        override fun getResetCode(continuation: ForgotPasswordContinuation?) {
            closeWaitWaitDialog()
            getResetPassword(continuation)
        }
    })



}


