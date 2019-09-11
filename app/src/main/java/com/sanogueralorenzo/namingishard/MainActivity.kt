package com.sanogueralorenzo.namingishard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.sanogueralorenzo.navigation.features.SampleNavigation
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import android.content.Intent;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity



class MainActivity : AppCompatActivity() {

    var mainActivity: MainActivity? = null
    var isVisible: Boolean? = false
    private val TAG = "MainActivity"
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivity = this;
        registerWithNotificationHubs();
        MyFirebaseMessagingService.createChannelAndHandleNotifications(getApplicationContext());

        // Access the device registration token (Firebase)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = "testje"
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
        FirebaseMessaging.getInstance().subscribeToTopic("PAYLOADTEST")

        startPosts()
    }

    private fun startPosts() = SampleNavigation.dynamicStart?.let { startActivity(it) }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show()
            } else {
//                Log.i(FragmentActivity.TAG, "This device is not supported by Google Play Services.")
//                ToastNotify("This device is not supported by Google Play Services.")
                finish()
            }
            return false
        }
        return true
    }

    fun registerWithNotificationHubs() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            val intent = Intent(this, RegistrationIntentService::class.java)
            startService(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        isVisible = true
    }

    override fun onPause() {
        super.onPause()
        isVisible = false
    }

    override fun onResume() {
        super.onResume()
        isVisible = true
    }

    override fun onStop() {
        super.onStop()
        isVisible = false
    }

    fun ToastNotify(notificationMessage: String) {
        runOnUiThread {
            Toast.makeText(this@MainActivity, notificationMessage, Toast.LENGTH_LONG).show()
            //val helloText = findViewById(R.id.text_hello) as TextView
            //helloText.text = notificationMessage
        }
    }
}
