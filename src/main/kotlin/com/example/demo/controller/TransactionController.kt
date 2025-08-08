package com.example.demo.controller

import com.example.demo.service.TransactionService
import com.example.demo.dto.TransactionRequest
import com.example.demo.dto.TransferRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TransactionController(private val transactionService: TransactionService) {

    @PostMapping("/transactions")
    fun createTransaction(@RequestBody request: TransactionRequest): ResponseEntity<Any> {
        return try {
            val transaction = when (request.type) {
                "DEPOSIT" -> transactionService.deposit(request.accountId, request.amount, request.description ?: "Depósito")
                "WITHDRAWAL" -> transactionService.withdraw(request.accountId, request.amount, request.description ?: "Saque")
                else -> throw IllegalArgumentException("Tipo de transação inválido.")
            }
            ResponseEntity.ok(transaction)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/transfers")
    fun transfer(@RequestBody request: TransferRequest): ResponseEntity<Any> {
        return try {
            val (outTransaction, inTransaction) = transactionService.transfer(
                request.fromAccountId,
                request.toAccountNumber,
                request.amount,
                request.description ?: "Transferência"
            )
            ResponseEntity.ok(mapOf(
                "outTransaction" to outTransaction,
                "inTransaction" to inTransaction,
                "message" to "Transferência realizada com sucesso"
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/accounts/{accountId}/transactions")
    fun getTransactions(@PathVariable accountId: Long): ResponseEntity<Any> {
        return try {
            val transactions = transactionService.getTransactionsByAccount(accountId)
            ResponseEntity.ok(transactions)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/accounts/{accountId}/transactions/recent")
    fun getRecentTransactions(@PathVariable accountId: Long): ResponseEntity<Any> {
        return try {
            val transactions = transactionService.getRecentTransactionsByAccount(accountId)
            ResponseEntity.ok(transactions)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
