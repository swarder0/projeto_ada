package com.example.demo.controller

import com.example.demo.service.ClientService
import com.example.demo.model.Client
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ClientController(private val clientService: ClientService) {

    @PostMapping("/clients")
    fun createClient(@RequestBody client: Client): ResponseEntity<Client> {
        val savedClient = clientService.createClient(client)
        return ResponseEntity.ok(savedClient)
    }

    @GetMapping("/clients")
    fun getAllClients(): ResponseEntity<List<Client>> {
        val clients = clientService.getAllClients()
        return ResponseEntity.ok(clients)
    }

    @GetMapping("/clients/{id}")
    fun getClientById(@PathVariable id: Long): ResponseEntity<Client> {
        val client = clientService.getClientById(id)
        return (if (client != null) {
            ResponseEntity.ok(client)
        } else {
            ResponseEntity.notFound().build()
        }) as ResponseEntity<Client>
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