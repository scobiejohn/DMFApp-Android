package au.com.dmf

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import au.com.dmf.login.LoginActivity
import au.com.dmf.utils.AWSManager
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import java.lang.Exception

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        AWSManager.init(applicationContext)
        val user = AWSManager.userPool?.currentUser
        val userName = user?.userId
        if (userName != null)  {
            user.getSessionInBackground(object: AuthenticationHandler{
                override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                    println("$userSession")
                    goSignIn()
                }

                override fun onFailure(exception: Exception?) {
                    println("FAILED")
                }

                override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                    println("$authenticationContinuation")
                }

                override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                    println("authenticationChallenge")
                }

                override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
                    println("getMFACode")
                }
            })
        } else {
            goSignIn()
        }
    }

    private fun goSignIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
