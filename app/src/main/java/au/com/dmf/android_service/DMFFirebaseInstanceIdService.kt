package au.com.dmf.android_service

import android.util.Log
import au.com.dmf.services.DynamoDBManager
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId

class DMFFirebaseInstanceIdService : FirebaseInstanceIdService() {

    companion object {
        private val TAG = DMFFirebaseInstanceIdService::class.java.simpleName
    }
    
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        DynamoDBManager.needRegisterFCMToken = true
        DynamoDBManager.refreshFCMToken = refreshedToken

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken)
    }

}
