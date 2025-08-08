package com.example.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "account_id")
    val account: Account,

    @Enumerated(EnumType.STRING)
    val type: TransactionType,

    val amount: Double,
    val description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val status: TransactionStatus = TransactionStatus.COMPLETED,

    // Para transferências
    val toAccountNumber: String? = null,
    val fromAccountNumber: String? = null
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_OUT,
    TRANSFER_IN
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}
