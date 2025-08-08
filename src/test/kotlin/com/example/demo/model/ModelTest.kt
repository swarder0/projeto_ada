package com.example.demo.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class AccountTest {
    @Test
    fun `deve criar account corretamente`() {
        // given
        val account = Account(id = 1, accountNumber = "123", balance = 100.0)
        // when, then
        assertEquals(1, account.id)
        assertEquals("123", account.accountNumber)
        assertEquals(100.0, account.balance)
    }
}

class ClientTest {
    @Test
    fun `deve criar client corretamente`() {
        // given
        val client = Client(
            id = 1,
            name = "Teste",
            email = "teste@email.com",
            cpf = "12345678900",
            birthDate = LocalDate.now(),
            address = Address(),
            phone = Phone(),
            password = "123456"
        )
        // when, then
        assertEquals("Teste", client.name)
        assertEquals("teste@email.com", client.email)
        assertEquals("12345678900", client.cpf)
        assertEquals("123456", client.password)
    }
}

class TransactionTest {
    @Test
    fun `deve criar transaction corretamente`() {
        // given
        val account = Account(id = 1, accountNumber = "123", balance = 100.0)
        val transaction = Transaction(
            id = 1,
            account = account,
            type = TransactionType.DEPOSIT,
            amount = 50.0,
            description = "Depósito",
            createdAt = LocalDateTime.now(),
            status = TransactionStatus.COMPLETED
        )
        // when, then
        assertEquals(1, transaction.id)
        assertEquals(account, transaction.account)
        assertEquals(TransactionType.DEPOSIT, transaction.type)
        assertEquals(50.0, transaction.amount)
        assertEquals("Depósito", transaction.description)
        assertEquals(TransactionStatus.COMPLETED, transaction.status)
    }
}
