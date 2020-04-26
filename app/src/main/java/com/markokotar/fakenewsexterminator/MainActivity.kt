package com.markokotar.fakenewsexterminator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Intent.ACTION_VIEW == intent.action) {

            val svc = Intent(this, OverlayShowingService::class.java)
            startService(svc)
            val intent = Intent(Intent.ACTION_VIEW, intent.data)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {

                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Chrome browser presumably not installed so allow user to choose instead
                //intent.setPackage(null)
                //startActivity(intent)
            }
            finish()
        }
        setContentView(R.layout.activity_main)
    }
}
