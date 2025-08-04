package com.example.demo.service

import org.springframework.stereotype.Service
import com.example.demo.model.Client
import com.example.demo.repository.ClientRepository

@Service
class ClientService(private val clientRepository: ClientRepository) {
    fun createClient(client: Client): Client {
        return clientRepository.save(client)
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
}