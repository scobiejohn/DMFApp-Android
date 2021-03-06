package au.com.brightcapital

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.com.brightcapital.login.LoginActivity
import au.com.brightcapital.utils.AWSManager
import au.com.brightcapital.utils.Constants
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler
import java.lang.Exception
import android.net.ConnectivityManager
import android.widget.Toast

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        if (!isNetworkAvailable()) {
            Toast.makeText(this@LaunchActivity, "Please make sure that your Android phone has network connectivity.",
                    Toast.LENGTH_LONG).show()

            return
        }

        AWSManager.init(applicationContext)
        val user = AWSManager.userPool?.currentUser
        val userName = user?.userId

        if (userName != null)  {

            user.getSessionInBackground(object: AuthenticationHandler{
                override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                    println("$userSession")

                    val idToken = userSession?.idToken?.jwtToken
                    println("idToken : $idToken")
                    val keyString = "cognito-idp.ap-southeast-2.amazonaws.com/" + Constants.CognitoIdentityUserPoolId

                    getUserDetails(userName, keyString, idToken!!)
                }

                override fun onFailure(exception: Exception?) {
                    println("FAILED")
                }

                override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                    println("$authenticationContinuation")
                    goSignIn()
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

    private fun getUserDetails(userName: String, loginKey: String, loginValue: String) {
        AWSManager.userPool?.getUser(userName)?.getDetailsInBackground(object : GetDetailsHandler {
            override fun onSuccess(cognitoUserDetails: CognitoUserDetails?) {
                println("$cognitoUserDetails")
                enterApp(loginKey, loginValue)
            }

            override fun onFailure(exception: Exception?) {
                println("Could not fetch user details")
                println(exception?.message)

                AWSManager.userPool?.currentUser?.signOut()
                goSignIn()
            }
        })
    }

    private fun enterApp(loginKey: String, loginValue: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("loginKey", loginKey)
        intent.putExtra("loginValue", loginValue)
        this.startActivity(intent)
    }

    private fun goSignIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
