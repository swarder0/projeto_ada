package com.example.demo.repository

import com.example.demo.model.Account
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AccountRepositoryTest {
    @Test
    fun `interface deve conter metodo findByAccountNumber`() {
        // given, when, then
        val methods = AccountRepository::class.java.methods.map { it.name }
        assertTrue(methods.contains("findByAccountNumber"))
    }
}
