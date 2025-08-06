package com.example.demo.repository

import com.example.demo.model.Client
import org.springframework.data.jpa.repository.JpaRepository

interface ClientRepository : JpaRepository<Client, Long> {
    fun existsByCpf(cpf: String): Boolean
    fun existsByEmail(email: String): Boolean
}
