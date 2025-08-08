package com.example.demo.controller

import com.example.demo.model.Client
import com.example.demo.service.ClientService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.time.LocalDate

class ClientControllerTest {
    private lateinit var clientService: ClientService
    private lateinit var controller: ClientController

    @BeforeEach
    fun setUp() {
        clientService = mockk(relaxed = true)
        controller = ClientController(clientService)
    }

    @Test
    fun `deve buscar todos os clientes`() {
        // given
        val clients = listOf<Client>()
        every { clientService.getAllClients() } returns clients
        // when
        val response = controller.getAllClients()
        // then
        assertEquals(ResponseEntity.ok(clients), response)
    }

    @Test
    fun `deve buscar cliente por id`() {
        // given
        val client = mockk<Client>()
        every { clientService.getClientById(1) } returns client
        // when
        val response = controller.getClientById(1)
        // then
        assertEquals(ResponseEntity.ok(client), response)
    }

    @Test
    fun `deve retornar not found se cliente nao existe`() {
        // given
        every { clientService.getClientById(1) } returns null
        // when
        val response = controller.getClientById(1)
        // then
        assertEquals(404, response.statusCode.value())
    }
}
