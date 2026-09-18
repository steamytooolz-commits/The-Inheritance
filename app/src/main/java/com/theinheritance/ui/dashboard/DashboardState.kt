package com.theinheritance.ui.dashboard

data class DashboardState(
    val day: Int = 1,
    val maxDays: Int = 30,
    val cashCents: Long = 420000,
    val businessName: String = "The Book Nook",
    val gmLine: String = "Your uncle died. The books are lying. You have 30 days."
)
