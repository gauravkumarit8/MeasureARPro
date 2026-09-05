package com.measurear.pro.core.ads

import com.measurear.pro.core.billing.EntitlementState
import kotlinx.coroutines.flow.first

/**
 * Wraps AdMob banner + rewarded ad requests. Every call gates on EntitlementState
 * first — Pro users never trigger an ad request. UMP consent flow (EU/UK) must
 * run before the first ad request; wire in Phase 2.
 */
class AdManager {
    suspend fun shouldShowAds(): Boolean = !EntitlementState.isPro.first()

    fun loadBanner() {
        // Phase 2: AdMob AdView + AdRequest, only called if shouldShowAds() == true.
        // Hidden during active AR measuring per Wireframe 1 (obstructs camera view).
    }

    fun showRewardedAd(onReward: () -> Unit) {
        // Phase 2: RewardedAd load + show. onReward() unlocks one watermark-free
        // export, one extra Fit Checker object, or a 24h Room Planner trial —
        // see PRD Section 4.
    }
}
