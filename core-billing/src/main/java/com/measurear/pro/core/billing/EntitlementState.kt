package com.measurear.pro.core.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth for Pro access, referenced by every feature module and
 * by :core-ads (to skip ad calls for Pro users). PRD Section 4/6: every Pro
 * feature and every ad request must check this before proceeding — do not
 * duplicate entitlement checks elsewhere.
 */
object EntitlementState {
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro

    internal fun update(isPro: Boolean) {
        _isPro.value = isPro
    }
}
