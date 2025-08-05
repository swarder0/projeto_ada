package com.example.demo.controller

import com.example.demo.dto.ClientDTO
import com.example.demo.model.Account
import com.example.demo.model.Address
import com.example.demo.model.Client
import com.example.demo.model.Phone
import com.example.demo.model.toDTO
import com.example.demo.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/clients")
class ClientController(private val clientService: ClientService) {

    @GetMapping
    fun getAllClients(): ResponseEntity<List<ClientDTO>> {
        val clients = clientService.getAllClients().map { it.toDTO() }
        return ResponseEntity.ok(clients)
    }

    @GetMapping("/{id}")
    fun getClientById(@PathVariable id: Long): ResponseEntity<ClientDTO> {
        val client = clientService.getClientById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(client.toDTO())
    }

    @PostMapping
    fun createClient(@RequestBody clientRequest: ClientRequest): ResponseEntity<ClientDTO> {
        try {
            // Converte a data de string para LocalDate
            val birthDate = LocalDate.parse(clientRequest.birthDate)

            // Cria objetos de endereço e telefone
            val address = Address(
                street = clientRequest.address.street,
                number = clientRequest.address.number,
                complement = clientRequest.address.complement ?: "",
                neighborhood = clientRequest.address.neighborhood,
                city = clientRequest.address.city,
                state = clientRequest.address.state,
                zipCode = clientRequest.address.zipCode
            )

            val phone = Phone(
                countryCode = clientRequest.phone.countryCode,
                areaCode = clientRequest.phone.areaCode,
                numberCode = clientRequest.phone.numberCode
            )

            // Cria o objeto cliente
            val client = Client(
                name = clientRequest.name,
                email = clientRequest.email,
                cpf = clientRequest.cpf,
                birthDate = birthDate,
                address = address,
                phone = phone,
                isActive = true
            )

            // Salva o cliente
            val savedClient = clientService.createClient(client)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedClient.toDTO())
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PutMapping("/{id}")
    fun updateClient(
        @PathVariable id: Long,
        @RequestBody clientRequest: ClientRequest
    ): ResponseEntity<ClientDTO> {
        try {
            val existingClient = clientService.getClientById(id) ?: return ResponseEntity.notFound().build()

            // Converte a data de string para LocalDate
            val birthDate = LocalDate.parse(clientRequest.birthDate)

            // Cria objetos de endereço e telefone
            val address = Address(
                street = clientRequest.address.street,
                number = clientRequest.address.number,
                complement = clientRequest.address.complement ?: "",
                neighborhood = clientRequest.address.neighborhood,
                city = clientRequest.address.city,
                state = clientRequest.address.state,
                zipCode = clientRequest.address.zipCode
            )

            val phone = Phone(
                countryCode = clientRequest.phone.countryCode,
                areaCode = clientRequest.phone.areaCode,
                numberCode = clientRequest.phone.numberCode
            )

            // Atualiza o cliente
            val updatedClient = existingClient.copy(
                name = clientRequest.name,
                email = clientRequest.email,
                cpf = clientRequest.cpf,
                birthDate = birthDate,
                address = address,
                phone = phone,
                isActive = clientRequest.isActive
            )

            val savedClient = clientService.updateClient(id, updatedClient) ?: return ResponseEntity.notFound().build()
            return ResponseEntity.ok(savedClient.toDTO())
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteClient(@PathVariable id: Long): ResponseEntity<Void> {
        try {
            clientService.deleteClient(id)
            return ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.notFound().build()
        }
    }

    // Classes de requisição
    data class AddressRequest(
        val street: String,
        val number: String,
        val complement: String?,
        val neighborhood: String,
        val city: String,
        val state: String,
        val zipCode: String
    )

    data class PhoneRequest(
        val countryCode: String,
        val areaCode: String,
        val numberCode: String
    )

    data class ClientRequest(
        val name: String,
        val email: String,
        val cpf: String,
        val birthDate: String,
        val address: AddressRequest,
        val phone: PhoneRequest,
        val isActive: Boolean = true
    )
}
