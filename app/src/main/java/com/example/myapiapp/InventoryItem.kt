package com.example.myapiapp

import java.util.Date

data class InventoryItem(
    val id: Int?,
    val inventoryNumber: String,
    val inventoryName: String,
    val entryDate: String,
    val locationRoom: String,
    val userId: Int
)
