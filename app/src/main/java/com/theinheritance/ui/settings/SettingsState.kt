package com.theinheritance.ui.settings

data class SettingsState(
    val remoteUrl: String = "",
    val remoteKey: String = "",
    val useRemote: Boolean = false,
    val isPro: Boolean = false
)
