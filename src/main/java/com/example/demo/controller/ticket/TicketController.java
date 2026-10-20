package com.example.demo.controller.ticket;

import com.example.demo.controller.dto.RequestActualizarEstadoDTO;
import com.example.demo.controller.dto.RequestCrearTicketDTO;
import com.example.demo.controller.dto.RequestReasignarPrioridadDTO;
import com.example.demo.model.ticket.Ticket;
import com.example.demo.service.ticket.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@CrossOrigin({"*"})
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(this.ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(this.ticketService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<Ticket> crearTicket(@RequestBody @Valid RequestCrearTicketDTO payload) {
        return ResponseEntity.status(201).body(this.ticketService.crearTicket(payload));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Ticket> actualizarEstadoPatch(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid RequestActualizarEstadoDTO payload,
            @RequestParam(required = false) String estado) {
        String estadoFinal = payload != null && payload.getEstado() != null ? payload.getEstado() : estado;
        return ResponseEntity.ok(this.ticketService.actualizarEstadoTicket(id, estadoFinal));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Ticket> actualizarEstadoPut(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid RequestActualizarEstadoDTO payload,
            @RequestParam(required = false) String estado) {
        String estadoFinal = payload != null && payload.getEstado() != null ? payload.getEstado() : estado;
        return ResponseEntity.ok(this.ticketService.actualizarEstadoTicket(id, estadoFinal));
    }

    @PatchMapping("/{id}/prioridad")
    public ResponseEntity<Ticket> reasignarPrioridadPatch(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid RequestReasignarPrioridadDTO payload,
            @RequestParam(required = false) String prioridad) {
        String prioridadFinal = payload != null && payload.getPrioridad() != null ? payload.getPrioridad() : prioridad;
        return ResponseEntity.ok(this.ticketService.reasignarPrioridad(id, prioridadFinal));
    }

    @PutMapping("/{id}/prioridad")
    public ResponseEntity<Ticket> reasignarPrioridadPut(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid RequestReasignarPrioridadDTO payload,
            @RequestParam(required = false) String prioridad) {
        String prioridadFinal = payload != null && payload.getPrioridad() != null ? payload.getPrioridad() : prioridad;
        return ResponseEntity.ok(this.ticketService.reasignarPrioridad(id, prioridadFinal));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Ticket>> obtenerTicketsUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(this.ticketService.obtenerTicketsUsuario(usuarioId));
    }
}
