package com.theinheritance.gm

sealed class ActionResult {
    data class Applied(val summary: String) : ActionResult()
    data class Rejected(val reason: String) : ActionResult()
}
