package com.theinheritance.monetization

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val INTERSTITIAL_TAG = "day_pass_interstitial"
    }

    private var interstitial: InterstitialAd? = null
    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        try {
            MobileAds.initialize(context) {}
        } catch (_: Exception) {
            // Ads are optional; the game plays fully without them.
        }
    }

    fun preloadInterstitial(adUnitId: String) {
        if (adUnitId.isBlank()) return
        try {
            InterstitialAd.load(
                context,
                adUnitId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitial = ad
                    }
                }
            )
        } catch (_: Exception) {
            interstitial = null
        }
    }

    fun takeInterstitial(): InterstitialAd? {
        val ad = interstitial
        interstitial = null
        return ad
    }
}
