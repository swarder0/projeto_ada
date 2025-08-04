package com.example.demo.controller

import com.example.demo.service.ClientService
import com.example.demo.model.Client
import com.example.demo.dto.ClientDTO
import com.example.demo.dto.AccountDTO
import com.example.demo.model.toDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
class ClientController(private val clientService: ClientService) {

    @PostMapping("/clients")
    fun createClient(@RequestBody client: Client): ResponseEntity<ClientDTO> {
        val savedClient = clientService.createClient(client)
        return ResponseEntity.ok(savedClient.toDTO())
    }

    @GetMapping("/clients")
    fun getAllClients(): ResponseEntity<List<ClientDTO>> {
        val clients = clientService.getAllClients().map { it.toDTO() }
        return ResponseEntity.ok(clients)
    }

    @GetMapping("/clients/{id}")
    fun getClientById(@PathVariable id: Long): ResponseEntity<ClientDTO> {
        val client = clientService.getClientById(id)
        return if (client != null) {
            ResponseEntity.ok(client.toDTO())
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
}