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
        // Bloqueia cadastro de dois CPFs diferentes para o mesmo email
        val existingByEmail = clientRepository.findAll().filter { it.email == client.email }
        if (existingByEmail.any { it.cpf != client.cpf }) {
            throw IllegalArgumentException("Não é permitido cadastrar dois CPFs diferentes para o mesmo email.")
        }
        if (clientRepository.existsByCpf(client.cpf)) {
            throw IllegalArgumentException("Já existe um cliente com este CPF.")
        }
        if (clientRepository.existsByEmail(client.email)) {
            // Se já existe o email, mas com o mesmo CPF, permite (atualização), senão bloqueia
            if (existingByEmail.any { it.cpf != client.cpf }) {
                throw IllegalArgumentException("Já existe um cliente com este email.")
            }
        }
        if (client.password.length < 6) {
            throw IllegalArgumentException("A senha deve ter pelo menos 6 dígitos.")
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
        val client = clientRepository.findAll().find { it.email == email }
            ?: throw IllegalArgumentException("Email ou senha inválidos.")
        if (client.password != password) {
            throw IllegalArgumentException("Email ou senha inválidos.")
        }
        return client
    }
}