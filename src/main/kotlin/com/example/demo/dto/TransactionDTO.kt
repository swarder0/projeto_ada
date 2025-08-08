package com.example.demo.dto

data class TransactionRequest(
    val accountId: Long,
    val amount: Double,
    val type: String, // DEPOSIT ou WITHDRAWAL
    val description: String?
)

data class TransferRequest(
    val fromAccountId: Long,
    val toAccountNumber: String,
    val amount: Double,
    val description: String?
)
