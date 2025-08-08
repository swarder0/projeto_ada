package com.example.demo.service

import com.example.demo.model.*
import com.example.demo.repository.AccountRepository
import com.example.demo.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    @Transactional
    fun deposit(accountId: Long, amount: Double, description: String): Transaction {
        if (amount <= 0) {
            throw IllegalArgumentException("O valor do depósito deve ser maior que zero.")
        }

        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Conta não encontrada.") }

        // Atualizar saldo da conta
        val updatedAccount = account.copy(balance = account.balance + amount)
        accountRepository.save(updatedAccount)

        // Criar transação
        val transaction = Transaction(
            account = updatedAccount,
            type = TransactionType.DEPOSIT,
            amount = amount,
            description = description,
            createdAt = LocalDateTime.now(),
            status = TransactionStatus.COMPLETED
        )

        return transactionRepository.save(transaction)
    }

    @Transactional
    fun withdraw(accountId: Long, amount: Double, description: String): Transaction {
        if (amount <= 0) {
            throw IllegalArgumentException("O valor do saque deve ser maior que zero.")
        }

        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Conta não encontrada.") }

        if (account.balance < amount) {
            throw IllegalArgumentException("Saldo insuficiente para realizar o saque.")
        }

        // Atualizar saldo da conta
        val updatedAccount = account.copy(balance = account.balance - amount)
        accountRepository.save(updatedAccount)

        // Criar transação
        val transaction = Transaction(
            account = updatedAccount,
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            description = description,
            createdAt = LocalDateTime.now(),
            status = TransactionStatus.COMPLETED
        )

        return transactionRepository.save(transaction)
    }

    @Transactional
    fun transfer(fromAccountId: Long, toAccountNumber: String, amount: Double, description: String): Pair<Transaction, Transaction> {
        if (amount <= 0) {
            throw IllegalArgumentException("O valor da transferência deve ser maior que zero.")
        }

        val fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow { IllegalArgumentException("Conta de origem não encontrada.") }

        val toAccount = accountRepository.findByAccountNumber(toAccountNumber)
            ?: throw IllegalArgumentException("Conta de destino não encontrada.")

        if (fromAccount.balance < amount) {
            throw IllegalArgumentException("Saldo insuficiente para realizar a transferência.")
        }

        if (fromAccount.accountNumber == toAccountNumber) {
            throw IllegalArgumentException("Não é possível transferir para a mesma conta.")
        }

        // Atualizar saldos
        val updatedFromAccount = fromAccount.copy(balance = fromAccount.balance - amount)
        val updatedToAccount = toAccount.copy(balance = toAccount.balance + amount)
        
        accountRepository.save(updatedFromAccount)
        accountRepository.save(updatedToAccount)

        // Criar transações
        val outTransaction = Transaction(
            account = updatedFromAccount,
            type = TransactionType.TRANSFER_OUT,
            amount = amount,
            description = description,
            createdAt = LocalDateTime.now(),
            status = TransactionStatus.COMPLETED,
            toAccountNumber = toAccountNumber
        )

        val inTransaction = Transaction(
            account = updatedToAccount,
            type = TransactionType.TRANSFER_IN,
            amount = amount,
            description = description,
            createdAt = LocalDateTime.now(),
            status = TransactionStatus.COMPLETED,
            fromAccountNumber = fromAccount.accountNumber
        )

        val savedOutTransaction = transactionRepository.save(outTransaction)
        val savedInTransaction = transactionRepository.save(inTransaction)

        return Pair(savedOutTransaction, savedInTransaction)
    }

    fun getTransactionsByAccount(accountId: Long): List<Transaction> {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
    }

    fun getRecentTransactionsByAccount(accountId: Long): List<Transaction> {
        return transactionRepository.findTop10ByAccountIdOrderByCreatedAtDesc(accountId)
    }
}