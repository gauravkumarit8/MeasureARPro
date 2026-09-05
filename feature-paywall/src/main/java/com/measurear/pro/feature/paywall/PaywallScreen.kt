package com.measurear.pro.feature.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Wireframe 4. Phase 2 work: wire BillingRepository (:core-billing) monthly
 * ($0.99) + lifetime ($2.99) purchase flows, 7-day trial, Restore Purchases.
 * Pricing flagged in PRD Section 4 for a post-launch review against real
 * conversion data.
 */
@Composable
fun PaywallScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MeasureAR Pro \u2014 Unlock Room Planner & More")
        Text("\u2714 Room & Floor Plan Mode")
        Text("\u2714 Fit Checker \u2014 full furniture library")
        Text("\u2714 Paint, Tile & Flooring Cost Estimator")
        Text("\u2714 Unlimited history, no watermark export")
        Text("\u2714 Offline QR plan sharing")
        Text("\u2714 100% Ad-Free Experience")
        Button(onClick = { /* Phase 2: BillingRepository.launchMonthlyPurchase(activity) */ }) {
            Text("Monthly: \$0.99/month")
        }
        Button(onClick = { /* Phase 2: BillingRepository.launchLifetimePurchase(activity) */ }) {
            Text("Lifetime: \$2.99 (Best Value)")
        }
        Button(onClick = { /* Phase 2: start 7-day trial via BillingClient */ }) {
            Text("START 7-DAY FREE TRIAL")
        }
    }
}
