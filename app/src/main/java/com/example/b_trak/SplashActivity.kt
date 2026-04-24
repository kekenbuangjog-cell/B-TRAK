package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * SYSTEM_BOOT: SplashActivity manages the application's initialization sequence.
 * It provides visual feedback for kernel authentication before transitioning to login.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // UI_BINDING: Identify boot components
        val progressBar = findViewById<ProgressBar>(R.id.splash_progress)
        val statusText = findViewById<TextView>(R.id.txt_loading_status)

        // PROTOCOL: MAIN_THREAD_HANDLER
        val handler = Handler(Looper.getMainLooper())

        /**
         * BOOT_SEQUENCE: Simulation of system loading.
         * Runs on a background thread to prevent UI freezing during initialization.
         */
        Thread {
            for (i in 0..100) {
                Thread.sleep(35) // SEQUENCE_TIMING: Adjust boot duration
                handler.post {
                    // UI_UPDATE: Reflect current system load progress
                    progressBar.progress = i
                    statusText.text = "AUTHENTICATING KERNEL... $i%"

                    // COMPLETION_PROTOCOL: Handover control to Authentication Station
                    if (i == 100) {
                        // SYSTEM_TRANSITION: Execute Intent to launch LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)

                        // TERMINATION: Dispose of SplashActivity to prevent back-navigation into boot
                        finish()
                    }
                }
            }
        }.start()
    }
}