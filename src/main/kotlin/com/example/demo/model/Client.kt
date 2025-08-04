package com.example.demo.model
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Client(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val cpf: String = "",
    val birthDate: LocalDate,
    @Embedded
    val address: Address,
    @Embedded
    val phone: Phone,
    val isActive: Boolean = true,
    @OneToOne(mappedBy = "client", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val account: Account? = null
)

@Embeddable
data class Phone(
    val countryCode: String = "",
    val areaCode: String = "",
    val numberCode: String = "",
)

@Embeddable
data class Address(
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = ""
)
