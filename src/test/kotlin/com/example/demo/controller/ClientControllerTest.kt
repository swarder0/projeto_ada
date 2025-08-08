package com.example.demo.controller

import com.example.demo.dto.ClientDTO
import com.example.demo.model.Address
import com.example.demo.model.Client
import com.example.demo.model.Phone
import com.example.demo.service.ClientService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.time.LocalDate

private val mockk: Any
    get() {
        TODO()
    }

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
        val clientDTOs = listOf<ClientDTO>()
        every { clientService.getAllClients() } returns listOf()
        // when
        val response = controller.getAllClients()
        // then
        assertEquals(ResponseEntity.ok(clientDTOs), response)
    }

//    @Test
//    fun `deve buscar cliente por id`() {
//        // given
//        val client = ClientDTO(
//            id = 1,
//            name = "Teste",
//            email = "teste@teste.com",
//            cpf = "12345678900",
//            birthDate = "25/03/1999",
//            address = Address(
//                street = "Rua Teste",
//                number = "123",
//                complement = "Apto 1",
//                neighborhood = "Bairro Teste",
//                city = "Cidade Teste",
//                state = "ST",
//                zipCode = "12345-678"
//            ),
//            phone = Phone(
//                countryCode = "55",
//                areaCode = "11",
//                numberCode = "91234",
//            ),
//            isActive = true,
//            account = null
//        )
//        every { clientService.getClientById(1) } returns client
//        // when
//        val response = controller.getClientById(1)
//        // then
//        val expectedDTO = ClientDTO(
//            id = 1,
//            name = "Teste",
//            email = "teste@teste.com",
//            cpf = "12345678900",
//            birthDate = null,
//            address = null,
//            phone = null,
//            isActive = true,
//            account = null
//        )
//        assertEquals(ResponseEntity.ok(expectedDTO), response)
//    }

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
