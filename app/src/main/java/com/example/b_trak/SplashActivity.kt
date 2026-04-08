package com.example.b_trak // <-- DOUBLE CHECK THIS MATCHES YOUR PROJECT

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.splash_progress)
        val statusText = findViewById<TextView>(R.id.txt_loading_status)

        // The "Brain" of the boot sequence
        val handler = Handler(Looper.getMainLooper())

        Thread {
            for (i in 0..100) {
                Thread.sleep(35) // Adjust this to make the boot faster or slower
                handler.post {
                    progressBar.progress = i
                    statusText.text = "AUTHENTICATING KERNEL... $i%"

                    if (i == 100) {
                        // This "Intent" tells the app: "Go from Splash to Login"
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)

                        // "finish()" is vital—it kills the splash screen so
                        // the user can't "Back" button into it again.
                        finish()
                    }
                }
            }
        }.start()
    }
}