package com.example.demo.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DtoTest {
    @Test
    fun `deve criar TransactionRequest corretamente`() {
        // given
        val dto = TransactionRequest(1, 100.0, "DEPOSIT", "desc")
        // when, then
        assertEquals(1, dto.accountId)
        assertEquals(100.0, dto.amount)
        assertEquals("DEPOSIT", dto.type)
        assertEquals("desc", dto.description)
    }

    @Test
    fun `deve criar TransferRequest corretamente`() {
        // given
        val dto = TransferRequest(1, "456", 50.0, "desc")
        // when, then
        assertEquals(1, dto.fromAccountId)
        assertEquals("456", dto.toAccountNumber)
        assertEquals(50.0, dto.amount)
        assertEquals("desc", dto.description)
    }
}
