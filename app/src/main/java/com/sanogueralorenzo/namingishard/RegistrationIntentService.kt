package com.sanogueralorenzo.namingishard

import android.app.IntentService
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.microsoft.windowsazure.messaging.NotificationHub
import java.util.concurrent.TimeUnit

class RegistrationIntentService : IntentService(TAG) {
    internal var FCM_token: String? = null

    private val hub: NotificationHub? = null

    override fun onHandleIntent(intent: Intent?) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var resultString: String? = null
        var regID: String? = null
        var storedToken: String? = null

        try {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                FCM_token = instanceIdResult.token
                Log.d(TAG, "FCM Registration Token: " + FCM_token!!)
            }
            TimeUnit.SECONDS.sleep(1)

            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server.
            // Otherwise, your server should have already received the token.
            storedToken = sharedPreferences.getString("FCMtoken", "")
            regID = sharedPreferences.getString("registrationID", null)
            if (regID == null) {

                val hub = NotificationHub(
                    NotificationSettings.HubName,
                    NotificationSettings.HubListenConnectionString, this
                )
                Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token!!)
                regID = hub.register(FCM_token).getRegistrationId()

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID!!
                Log.d(TAG, resultString)

                sharedPreferences.edit().putString("registrationID", regID).apply()
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply()
            } else if (storedToken !== FCM_token) {

                val hub = NotificationHub(
                    NotificationSettings.HubName,
                    NotificationSettings.HubListenConnectionString, this
                )
                Log.d(TAG, "NH Registration refreshing with token : " + FCM_token!!)
                regID = hub.register(FCM_token).getRegistrationId()

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID!!
                Log.d(TAG, resultString)

                sharedPreferences.edit().putString("registrationID", regID).apply()
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply()
            } else {
                resultString = "Previously Registered Successfully - RegId : " + regID!!
            }// Check to see if the token has been compromised and needs refreshing.
        } catch (e: Exception) {
            //Log.e(TAG, resultString = "Failed to complete registration", e)
            // If an exception happens while fetching the new token or updating registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
    }

    companion object {

        private val TAG = "RegIntentService"
    }
}