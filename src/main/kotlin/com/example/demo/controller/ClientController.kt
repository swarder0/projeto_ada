package com.example.demo.controller

import com.example.demo.model.Client
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ClientController(private val clientService: ClientService) {

        return ResponseEntity.ok(clients)
    }

    }
        }
    }
    fun deleteClient(@PathVariable id: Long): ResponseEntity<Void> {
            clientService.deleteClient(id)
        } catch (e: IllegalArgumentException) {
        }
    }
}