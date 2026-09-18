package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fraud_profile")
data class FraudProfileEntity(
    @PrimaryKey val id: Int = 1,
    val primaryScheme: String,
    val secondaryScheme: String?,
    val tertiaryScheme: String?,
    val hiddenTruthType: String,
    val hiddenTruthNpcId: Long?,
    val hiddenTruthAmountCents: Long,
    val isFullyDiscovered: Boolean = false
)
