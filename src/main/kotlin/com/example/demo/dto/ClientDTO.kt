package com.example.demo.dto

import com.example.demo.model.Address
import com.example.demo.model.Phone

// DTO para Account sem o campo client
data class AccountDTO(
    val id: Long?,
    val accountNumber: String?,
    val balance: Double?
)

// DTO para Client sem recursividade
data class ClientDTO(
    val id: Long?,
    val name: String?,
    val email: String?,
    val cpf: String?,
    val birthDate: String?,
    val address: Address?,
    val phone: Phone?,
    val isActive: Boolean?,
    val account: AccountDTO?
)

