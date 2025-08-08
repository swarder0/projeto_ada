package com.example.demo.repository

import com.example.demo.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByAccountIdOrderByCreatedAtDesc(accountId: Long): List<Transaction>
    fun findTop10ByAccountIdOrderByCreatedAtDesc(accountId: Long): List<Transaction>
}
