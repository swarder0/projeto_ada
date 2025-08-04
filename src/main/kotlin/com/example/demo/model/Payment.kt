package com.example.demo.model

import java.util.Date
import javax.xml.crypto.Data

class Payment (
    val id: Long = 0,
    val clientId: Long = 0,
    val accountId: Long = 0,
    val amount: Double = 0.0,
    val description: String = "",
    val date: Date = Date(),
    val isSuccessful: Boolean = true
) {
    fun getFormattedAmount(): String {
        return String.format("%.2f", amount)
    }
}