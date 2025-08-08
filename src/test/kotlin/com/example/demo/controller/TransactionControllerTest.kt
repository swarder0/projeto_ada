package com.example.demo.controller

import com.example.demo.dto.TransactionRequest
import com.example.demo.dto.TransferRequest
import com.example.demo.model.*
import com.example.demo.service.TransactionService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class TransactionControllerTest {
    private lateinit var transactionService: TransactionService
    private lateinit var controller: TransactionController

    @BeforeEach
    fun setUp() {
        transactionService = mockk(relaxed = true)
        controller = TransactionController(transactionService)
    }

    @Test
    fun `deve criar deposito com sucesso`() {
        // given
        val request = TransactionRequest(1, 100.0, "DEPOSIT", "Depósito")
        val transaction = mockk<Transaction>()
        every { transactionService.deposit(1, 100.0, "Depósito") } returns transaction
        // when
        val response = controller.createTransaction(request)
        // then
        assertEquals(ResponseEntity.ok(transaction), response)
    }

    @Test
    fun `deve retornar erro para tipo de transacao invalido`() {
        // given
        val request = TransactionRequest(1, 100.0, "INVALID", "Teste")
        // when
        val response = controller.createTransaction(request)
        // then
        assertEquals(400, response.statusCode.value())
        assertTrue(response?.body.toString().contains("Tipo de transação inválido"))
    }

//    @Test
//    fun `deve criar transferencia com sucesso`() {
//        // given
//        val request = TransferRequest(1, "456", 50.0, "Transferência")
//        val outTransaction = mockk<Transaction>()
//        val inTransaction = mockk<Transaction>()
//        every { transactionService.transfer(1, "456", 50.0, "Transferência") } returns Pair(outTransaction, inTransaction)
//        // when
//        val response = controller.transfer(request)
//        // then
//        assertEquals(200, response.statusCode.value())
//        val body = response.body as Map<*, *>
//        assertEquals(outTransaction, body["outTransaction"])
//        assertEquals(inTransaction, body["inTransaction"])
//    }
}
