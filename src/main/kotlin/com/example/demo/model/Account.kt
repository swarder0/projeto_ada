package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "accounts")
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val accountNumber: String = "",
    val balance: Double = 0.0,
    @OneToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    val client: Client? = null
)
