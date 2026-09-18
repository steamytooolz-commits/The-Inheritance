package com.theinheritance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: Long,
    val code: String,
    val name: String,
    val type: String,
    val isContra: Boolean = false,
    val parentAccountId: Long? = null,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false
)
