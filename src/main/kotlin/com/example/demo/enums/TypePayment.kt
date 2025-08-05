package com.example.demo.enums

enum class TypePayment {
    CREDIT,
    DEBIT,
    PIX,
    TRANSFER,
    WITHDRAWAL,
    DEPOSIT;

    override fun toString(): String {
        return when (this) {
            CREDIT -> "Credit"
            DEBIT -> "Debit"
            PIX -> "Pix"
            TRANSFER -> "Transfer"
            WITHDRAWAL -> "Withdrawal"
            DEPOSIT -> "Deposit"
        }
    }

}