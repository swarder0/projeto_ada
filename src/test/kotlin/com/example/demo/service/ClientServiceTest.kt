package com.example.demo.service

import com.example.demo.model.*
import com.example.demo.repository.ClientRepository
import com.example.demo.repository.AccountRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class ClientServiceTest {
    private lateinit var clientRepository: ClientRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var clientService: ClientService

    @BeforeEach
    fun setUp() {
        clientRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        clientService = ClientService(clientRepository, accountRepository)
    }

    @Test
    fun `deve criar cliente com sucesso`() {
        // given
        val client = Client(
            id = 0,
            name = "João",
            email = "joao@email.com",
            cpf = "12345678900",
            birthDate = LocalDate.now(),
            address = mockk(),
            phone = mockk(),
            password = "123456"
        )
        every { clientRepository.existsByCpf(client.cpf) } returns false
        every { clientRepository.findByEmail(client.email) } returns null
        val slotClient = slot<Client>()
        every { clientRepository.save(capture(slotClient)) } answers { slotClient.captured }
        val slotAccount = slot<Account>()
        every { accountRepository.save(capture(slotAccount)) } answers { slotAccount.captured }

        // when
        val savedClient = clientService.createClient(client)

        // then
        assertEquals(client.name, savedClient.name)
        assertEquals(client.email, savedClient.email)
        assertEquals(client.cpf, savedClient.cpf)
        assertNotNull(slotAccount.captured.accountNumber)
        assertEquals(0.0, slotAccount.captured.balance)
    }

//    @Test
//    fun `nao deve criar cliente com senha curta`() {
//        // given
//        val client = Client(
//            id = 0,
//            name = "Maria",
//            email = "maria@email.com",
//            cpf = "98765432100",
//            birthDate = LocalDate.now(),
//            address = mockk(),
//            phone = mockk(),
//            password = "123"
//        )
//        // when, then
//        val exception = assertThrows(IllegalArgumentException::class.java) {
//            clientService.createClient(client)
//        }
//        assertEquals("A senha deve ter pelo menos 6 dígitos.", exception.message)
//    }

    @Test
    fun `nao deve criar cliente com cpf duplicado`() {
        // given
        val client = Client(
            id = 0,
            name = "Carlos",
            email = "carlos@email.com",
            cpf = "11122233344",
            birthDate = LocalDate.now(),
            address = mockk(),
            phone = mockk(),
            password = "123456"
        )
        every { clientRepository.existsByCpf(client.cpf) } returns true
        // when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            clientService.createClient(client)
        }
        assertEquals("Já existe um cliente com este CPF.", exception.message)
    }

    @Test
    fun `nao deve criar cliente com email para cpf diferente`() {
        // given
        val client = Client(
            id = 0,
            name = "Ana",
            email = "ana@email.com",
            cpf = "22233344455",
            birthDate = LocalDate.now(),
            address = mockk(),
            phone = mockk(),
            password = "123456"
        )
        val existing = Client(
            id = 2,
            name = "Outra Ana",
            email = "ana@email.com",
            cpf = "99988877766",
            birthDate = LocalDate.now(),
            address = mockk(),
            phone = mockk(),
            password = "123456"
        )
        every { clientRepository.existsByCpf(client.cpf) } returns false
        every { clientRepository.findByEmail(client.email) } returns existing
        // when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            clientService.createClient(client)
        }
        assertEquals("Não é permitido cadastrar dois CPFs diferentes para o mesmo email.", exception.message)
    }

    @Test
    fun `deve buscar todos os clientes`() {
        // given
        val clients = listOf(
            Client(1, "A", "a@email.com", "1", LocalDate.now(), mockk(), mockk(), true, null, "123456"),
            Client(2, "B", "b@email.com", "2", LocalDate.now(), mockk(), mockk(), true, null, "123456")
        )
        every { clientRepository.findAll() } returns clients
        // when
        val result = clientService.getAllClients()
        // then
        assertEquals(2, result.size)
    }

    @Test
    fun `deve buscar cliente por id`() {
        // given
        val client = Client(1, "A", "a@email.com", "1", LocalDate.now(), mockk(), mockk(), true, null, "123456")
        every { clientRepository.findById(1) } returns Optional.of(client)
        // when
        val result = clientService.getClientById(1)
        // then
        assertEquals(client, result)
    }

    @Test
    fun `deve atualizar cliente existente`() {
        // given
        val client = Client(1, "A", "a@email.com", "1", LocalDate.now(), mockk(), mockk(), true, null, "123456")
        every { clientRepository.existsById(1) } returns true
        every { clientRepository.save(any()) } returns client
        // when
        val result = clientService.updateClient(1, client)
        // then
        assertEquals(client, result)
    }

    @Test
    fun `nao deve atualizar cliente inexistente`() {
        // given
        val client = Client(1, "A", "a@email.com", "1", LocalDate.now(), mockk(), mockk(), true, null, "123456")
        every { clientRepository.existsById(1) } returns false
        // when
        val result = clientService.updateClient(1, client)
        // then
        assertNull(result)
    }

    @Test
    fun `deve deletar cliente existente`() {
        // given
        every { clientRepository.existsById(1) } returns true
        every { clientRepository.deleteById(1) } returns Unit
        // when, then
        assertDoesNotThrow { clientService.deleteClient(1) }
    }

    @Test
    fun `nao deve deletar cliente inexistente`() {
        // given
        every { clientRepository.existsById(1) } returns false
        // when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            clientService.deleteClient(1)
        }
        assertEquals("Client with id 1 does not exist", exception.message)
    }

    @Test
    fun `deve fazer login com sucesso`() {
        // given
        val client = Client(1, "A", "a@email.com", "1", LocalDate.now(), mockk(), mockk(), true, null, "123456")
        every { clientRepository.findByEmail("a@email.com") } returns client
        // when
        val result = clientService.login("a@email.com", "123456")
        // then
        assertEquals(client, result)
    }

    @Test
    fun `nao deve fazer login com senha curta`() {
        // given, when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            clientService.login("a@email.com", "123")
        }
        assertEquals("A senha deve ter pelo menos 6 dígitos.", exception.message)
    }

    @Test
    fun `nao deve fazer login com email inexistente`() {
        // given
        every { clientRepository.findByEmail("naoexiste@email.com") } returns null
        // when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            clientService.login("naoexiste@email.com", "123456")
        }
        assertEquals("Email ou senha inválidos.", exception.message)
    }

    @Test
    fun `nao deve fazer login com senha errada`() {
        // given
        val client = Client(1, "A", "a@email.com", "1", LocalDate.now(), mockk(), mockk(), true, null, "123456")
        every { clientRepository.findByEmail("a@email.com") } returns client
        // when, then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            clientService.login("a@email.com", "errada")
        }
        assertEquals("Email ou senha inválidos.", exception.message)
    }
}
