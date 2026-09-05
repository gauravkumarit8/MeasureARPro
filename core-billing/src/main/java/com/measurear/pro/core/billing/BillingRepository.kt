package com.measurear.pro.core.billing

/**
 * Wraps Play Billing Library. Phase 2 work: wire real BillingClient calls,
 * purchase verification, and subscription/lifetime SKU handling here, then
 * call EntitlementState.update(...) on every purchase/restore result.
 */
interface BillingRepository {
    suspend fun queryPurchases()
    suspend fun launchMonthlyPurchase(activity: android.app.Activity)
    suspend fun launchLifetimePurchase(activity: android.app.Activity)
    suspend fun restorePurchases()
}
