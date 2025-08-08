package com.example.demo.controller

import com.example.demo.service.ClientService
import com.example.demo.model.Client
import com.example.demo.dto.ClientDTO
import com.example.demo.dto.AccountDTO
import com.example.demo.dto.LoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ClientController(private val clientService: ClientService) {

    @PostMapping("/clients")
    fun createClient(@RequestBody client: Client): ResponseEntity<Any> {
        return try {
            val savedClient = clientService.createClient(client)
            val account = savedClient.account?.let {
                AccountDTO(it.id, it.accountNumber, it.balance)
            }
            val clientDTO = ClientDTO(
                savedClient.id,
                savedClient.name,
                savedClient.email,
                savedClient.cpf,
                savedClient.birthDate?.toString(),
                savedClient.address,
                savedClient.phone,
                savedClient.isActive,
                account
            )
            ResponseEntity.ok(clientDTO)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/clients")
    fun getAllClients(): ResponseEntity<List<ClientDTO>> {
        val clients = clientService.getAllClients()
        val clientDTOs = clients.map { client ->
            val account = client.account?.let {
                AccountDTO(it.id, it.accountNumber, it.balance)
            }
            ClientDTO(
                client.id,
                client.name,
                client.email,
                client.cpf,
                client.birthDate?.toString(),
                client.address,
                client.phone,
                client.isActive,
                account
            )
        }
        return ResponseEntity.ok(clientDTOs)
    }

    @GetMapping("/clients/{id}")
    fun getClientById(@PathVariable id: Long): ResponseEntity<ClientDTO> {
        val client = clientService.getClientById(id)
        return if (client != null) {
            val account = client.account?.let {
                AccountDTO(it.id, it.accountNumber, it.balance)
            }
            val clientDTO = ClientDTO(
                client.id,
                client.name,
                client.email,
                client.cpf,
                client.birthDate?.toString(),
                client.address,
                client.phone,
                client.isActive,
                account
            )
            ResponseEntity.ok(clientDTO)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/clients/{id}")
    fun updateClient(@PathVariable id: Long, @RequestBody client: Client): ResponseEntity<Client> {
        val updatedClient = clientService.updateClient(id, client)
        return if (updatedClient != null) {
            ResponseEntity.ok(updatedClient)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/clients/{id}")
    fun deleteClient(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            clientService.deleteClient(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        return try {
            val client = clientService.login(loginRequest.email, loginRequest.password)
            val account = client.account?.let {
                AccountDTO(it.id, it.accountNumber, it.balance)
            }
            val clientDTO = ClientDTO(
                client.id,
                client.name,
                client.email,
                client.cpf,
                client.birthDate?.toString(),
                client.address,
                client.phone,
                client.isActive,
                account
            )
            ResponseEntity.ok(clientDTO)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}