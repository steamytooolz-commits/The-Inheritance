package com.theinheritance

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TheInheritanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ads are optional; the game plays fully without them.
        try {
            MobileAds.initialize(this) {}
        } catch (_: Exception) {
        }
    }
}
