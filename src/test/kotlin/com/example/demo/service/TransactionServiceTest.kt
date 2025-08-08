package com.example.demo.service

import com.example.demo.model.*
import com.example.demo.repository.AccountRepository
import com.example.demo.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class TransactionServiceTest {
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun setUp() {
        transactionRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        transactionService = TransactionService(transactionRepository, accountRepository)
    }

    @Test
    fun `deve realizar deposito com sucesso`() {
        // given
        val account = Account(id = 1, accountNumber = "123", balance = 100.0)
        every { accountRepository.findById(1) } returns Optional.of(account)
        val slotAccount = slot<Account>()
        every { accountRepository.save(capture(slotAccount)) } answers { slotAccount.captured }
        val slotTransaction = slot<Transaction>()
        every { transactionRepository.save(capture(slotTransaction)) } answers { slotTransaction.captured }

        // when
        val transaction = transactionService.deposit(1, 50.0, "Depósito Teste")

        // then
        assertEquals(150.0, slotAccount.captured.balance)
        assertEquals(TransactionType.DEPOSIT, transaction.type)
        assertEquals(50.0, transaction.amount)
        assertEquals("Depósito Teste", transaction.description)
        assertNotNull(transaction.createdAt)
        assertEquals(TransactionStatus.COMPLETED, transaction.status)
    }

    @Test
    fun `nao deve permitir deposito com valor negativo`() {
        // given, when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            transactionService.deposit(1, -10.0, "Depósito Negativo")
        }
        assertEquals("O valor do depósito deve ser maior que zero.", exception.message)
    }

    @Test
    fun `deve realizar saque com sucesso`() {
        // given
        val account = Account(id = 1, accountNumber = "123", balance = 100.0)
        every { accountRepository.findById(1) } returns Optional.of(account)
        val slotAccount = slot<Account>()
        every { accountRepository.save(capture(slotAccount)) } answers { slotAccount.captured }
        val slotTransaction = slot<Transaction>()
        every { transactionRepository.save(capture(slotTransaction)) } answers { slotTransaction.captured }

        // when
        val transaction = transactionService.withdraw(1, 40.0, "Saque Teste")

        // then
        assertEquals(60.0, slotAccount.captured.balance)
        assertEquals(TransactionType.WITHDRAWAL, transaction.type)
        assertEquals(40.0, transaction.amount)
        assertEquals("Saque Teste", transaction.description)
        assertNotNull(transaction.createdAt)
        assertEquals(TransactionStatus.COMPLETED, transaction.status)
    }

    @Test
    fun `nao deve permitir saque com saldo insuficiente`() {
        // given
        val account = Account(id = 1, accountNumber = "123", balance = 20.0)
        every { accountRepository.findById(1) } returns Optional.of(account)
        // when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            transactionService.withdraw(1, 50.0, "Saque Teste")
        }
        assertEquals("Saldo insuficiente para realizar o saque.", exception.message)
    }

//    @Test
//    fun `deve realizar transferencia com sucesso`() {
//        // given
//        val address = Address(street = "Rua Teste", number = "123", city = "Cidade", state = "ST", zipCode = "00000-000")
//        val phone = Phone(countryCode = "+55", areaCode = "11", numberCode = "999999999")
//        val clientFrom = Client(
//            id = 1,
//            name = "Cliente Origem",
//            email = "origem@email.com",
//            cpf = "12345678900",
//            birthDate = LocalDate.now().minusYears(30),
//            address = address,
//            phone = phone,
//            password = "123456"
//        )
//        val clientTo = Client(
//            id = 2,
//            name = "Cliente Destino",
//            email = "destino@email.com",
//            cpf = "09876543211",
//            birthDate = LocalDate.now().minusYears(25),
//            address = address,
//            phone = phone,
//            password = "654321"
//        )
//        val fromAccount = Account(id = 1, accountNumber = "123", balance = 200.0, client = clientFrom)
//        val toAccount = Account(id = 2, accountNumber = "456", balance = 50.0, client = clientTo)
//        every { accountRepository.findById(1) } returns Optional.of(fromAccount)
//        every { accountRepository.findByAccountNumber("456") } returns toAccount
//        val slotFrom = slot<Account>()
//        val slotTo = slot<Account>()
//        every { accountRepository.save(capture(slotFrom)) } answers { slotFrom.captured }
//        every { accountRepository.save(capture(slotTo)) } answers { slotTo.captured }
//        val slotOut = slot<Transaction>()
//        val slotIn = slot<Transaction>()
//        every { transactionRepository.save(capture(slotOut)) } answers { slotOut.captured }
//        every { transactionRepository.save(capture(slotIn)) } answers { slotIn.captured }
//
//        // when
//        val (outTransaction, inTransaction) = transactionService.transfer(1, "456", 100.0, "Transferência Teste")
//
//        // then
//        assertEquals(100.0, slotFrom.captured.balance)
//        assertEquals(150.0, slotTo.captured.balance)
//        assertEquals(TransactionType.TRANSFER_OUT, outTransaction.type)
//        assertEquals(TransactionType.TRANSFER_IN, inTransaction.type)
//        assertEquals(100.0, outTransaction.amount)
//        assertEquals(100.0, inTransaction.amount)
//        assertEquals("Transferência Teste", outTransaction.description)
//        assertEquals("Transferência Teste", inTransaction.description)
//    }

    @Test
    fun `nao deve permitir transferencia para mesma conta`() {
        // given
        val fromAccount = Account(id = 1, accountNumber = "123", balance = 200.0)
        every { accountRepository.findById(1) } returns Optional.of(fromAccount)
        every { accountRepository.findByAccountNumber("123") } returns fromAccount
        // when, then000
        val exception = assertThrows(IllegalArgumentException::class.java) {
            transactionService.transfer(1, "123", 50.0, "Transferência Inválida")
        }
        assertEquals("Não é possível transferir para a mesma conta.", exception.message)
    }

    @Test
    fun `deve buscar transacoes por conta`() {
        // given
        val accountId = 1L
        val transactions = listOf(
            Transaction(id = 1, account = Account(id = 1, accountNumber = "123", balance = 100.0), type = TransactionType.DEPOSIT, amount = 50.0, description = "Depósito", createdAt = LocalDateTime.now()),
            Transaction(id = 2, account = Account(id = 1, accountNumber = "123", balance = 150.0), type = TransactionType.WITHDRAWAL, amount = 20.0, description = "Saque", createdAt = LocalDateTime.now())
        )
        every { transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId) } returns transactions
        // when
        val result = transactionService.getTransactionsByAccount(accountId)
        // then
        assertEquals(2, result.size)
        assertEquals(TransactionType.DEPOSIT, result[0].type)
        assertEquals(TransactionType.WITHDRAWAL, result[1].type)
    }

    @Test
    fun `deve buscar ultimas 10 transacoes por conta`() {
        // given
        val accountId = 1L
        val transactions = List(10) {
            Transaction(id = it.toLong(), account = Account(id = 1, accountNumber = "123", balance = 100.0), type = TransactionType.DEPOSIT, amount = 10.0, description = "Depósito $it", createdAt = LocalDateTime.now())
        }
        every { transactionRepository.findTop10ByAccountIdOrderByCreatedAtDesc(accountId) } returns transactions
        // when
        val result = transactionService.getRecentTransactionsByAccount(accountId)
        // then
        assertEquals(10, result.size)
    }
}
