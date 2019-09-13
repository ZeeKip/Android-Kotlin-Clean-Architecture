package com.sanogueralorenzo.namingishard

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.sanogueralorenzo.navigation.features.SampleNavigation
import java.io.File
import java.io.PrintWriter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                writeFileTxtTest()
            })
        FirebaseMessaging.getInstance().subscribeToTopic("PAYLOADTEST")

        startPosts()
    }

    private fun startPosts() = SampleNavigation.dynamicStart?.let { startActivity(it) }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun writeFileTxtTest() {
        //SAVE TEXT FILE
        val sd_main = File("${Environment.getExternalStorageDirectory()}/testlocation")
        var success = true
        if (!sd_main.exists()) {
            success = sd_main.mkdirs()
        }
        if (success) {
            val sd = File("filename.txt")

            if (!sd.exists()) {
                success = sd.mkdir()
            }
            if (success) {
                // directory exists or already created
                val dest = File(sd, "testfile")
                try {
                    // response is the data written to file
                    Toast.makeText(baseContext, "SAVING TEXT", Toast.LENGTH_LONG).show()
                    PrintWriter(dest)
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "FAILED SAVING TEXT", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(baseContext, "${Environment.getExternalStorageDirectory()}/testlocation", Toast.LENGTH_LONG).show()
            }
        }
    }
}
