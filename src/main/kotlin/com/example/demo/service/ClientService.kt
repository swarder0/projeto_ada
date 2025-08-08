package com.example.demo.service

import org.springframework.stereotype.Service
import com.example.demo.model.Client
import com.example.demo.repository.ClientRepository
import com.example.demo.model.Account
import com.example.demo.repository.AccountRepository
import java.util.UUID

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val accountRepository: AccountRepository
) {
    fun createClient(client: Client): Client {
        // Validação de senha obrigatória
        if (client.password.length < 6) {
            throw IllegalArgumentException("A senha deve ter pelo menos 6 dígitos.")
        }

        // Bloqueia cadastro se já existe CPF
        if (clientRepository.existsByCpf(client.cpf)) {
            throw IllegalArgumentException("Já existe um cliente com este CPF.")
        }

        // Bloqueia cadastro de dois CPFs diferentes para o mesmo email
        val existingByEmail = clientRepository.findByEmail(client.email)
        if (existingByEmail != null && existingByEmail.cpf != client.cpf) {
            throw IllegalArgumentException("Não é permitido cadastrar dois CPFs diferentes para o mesmo email.")
        }

        val accountNumber = UUID.randomUUID().toString().substring(0, 10)
        val account = Account(accountNumber = accountNumber, balance = 0.0)
        val clientWithAccount = client.copy(account = account)
        val savedClient = clientRepository.save(clientWithAccount)
        val savedAccount = account.copy(client = savedClient)
        accountRepository.save(savedAccount)
        return savedClient
    }
    fun getAllClients(): List<Client> {
        return clientRepository.findAll()
    }
    fun getClientById(id: Long): Client? {
        return clientRepository.findById(id).orElse(null)
    }
    fun updateClient(id: Long, client: Client): Client? {
        return if (clientRepository.existsById(id)) {
            clientRepository.save(client.copy(id = id))
        } else {
            null
        }
    }
    fun deleteClient(id: Long) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id)
        } else {
            throw IllegalArgumentException("Client with id $id does not exist")
        }
    }
    fun login(email: String, password: String): Client {
        // Validação de senha mínima no login
        if (password.length < 6) {
            throw IllegalArgumentException("A senha deve ter pelo menos 6 dígitos.")
        }

        val client = clientRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Email ou senha inválidos.")

        if (client.password != password) {
            throw IllegalArgumentException("Email ou senha inválidos.")
        }
        return client
    }
}