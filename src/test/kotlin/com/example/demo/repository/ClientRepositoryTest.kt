package com.example.demo.repository

import com.example.demo.model.Client
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientRepositoryTest {
    @Test
    fun `interface deve conter metodos customizados`() {
        // given, when, then
        val methods = ClientRepository::class.java.methods.map { it.name }
        assertTrue(methods.contains("existsByCpf"))
        assertTrue(methods.contains("existsByEmail"))
        assertTrue(methods.contains("findByEmail"))
        assertTrue(methods.contains("findByCpf"))
    }
}
