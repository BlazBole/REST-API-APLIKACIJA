package com.example.myapiapp

import java.util.Date

data class InventoryItem(
    val id: Int?,
    val inventoryNumber: Int,
    val inventoryName: String,
    val entryDate: Date,
    val locationId: Int,
    val userId: Int
)
