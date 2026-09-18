package com.theinheritance.gm

/** Drama comes from constraint: the GM may execute at most this many actions per turn. */
data class GmActionBudget(val maxPerTurn: Int = 15)
