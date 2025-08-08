package com.example.demo.repository

import com.example.demo.model.Transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TransactionRepositoryTest {
    @Test
    fun `interface deve conter metodos customizados`() {
        // given, when, then
        val methods = TransactionRepository::class.java.methods.map { it.name }
        assertTrue(methods.contains("findByAccountIdOrderByCreatedAtDesc"))
        assertTrue(methods.contains("findTop10ByAccountIdOrderByCreatedAtDesc"))
    }
}
