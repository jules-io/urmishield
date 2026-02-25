package com.urmilabs.shield.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "number_list")
data class NumberListEntity(
    @PrimaryKey val number: String,
    val type: String, // WHITELIST, BLACKLIST, GRAYLIST
    val label: String // e.g. "Doctor", "Known Spammer"
)
