package au.com.dmf.utils

import android.content.Context
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession

class AWSManager {

    companion object {

        var userPool: CognitoUserPool? = null
        var currentSession: CognitoUserSession? = null
        var userDetails: CognitoUserDetails? = null
        var user: String? = null
        var firstTimeLoginNewPassword: String? = null

        fun init(context: Context) {
            if (userPool != null) {
                return
            }

            if (userPool == null) {
                //userPoolId, clientId, clientSecret, cognitoRegion
                userPool = CognitoUserPool(context, Constants.CognitoIdentityUserPoolId, Constants.CognitoIdentityUserPoolAppClientId,
                        Constants.CognitoIdentityUserPoolAppClientSecret, Constants.COGNITO_REGIONTYPE)

                // This will also work
                /*
                ClientConfiguration clientConfiguration = new ClientConfiguration();
                AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
                cipClient.setRegion(Region.getRegion(cognitoRegion));
                userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cipClient);
                */
            }

        }

        fun formatException(exception: Exception): String {
            var formattedString = "Internal Error"
            Log.e("App Error", exception.toString())
            Log.getStackTraceString(exception)

            val temp = exception.message

            if (temp != null && temp.length > 0) {
                formattedString = temp.split("\\(".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                if (temp != null && temp.length > 0) {
                    return formattedString
                }
            }

            return formattedString
        }

    }

}