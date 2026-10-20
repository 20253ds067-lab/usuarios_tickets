package com.example.demo.controller.usuario;

import com.example.demo.controller.dto.RequestCrearUsuarioDTO;
import com.example.demo.model.ticket.Ticket;
import com.example.demo.model.usuario.Usuario;
import com.example.demo.service.ticket.TicketService;
import com.example.demo.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin({"*"})
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TicketService ticketService;

    public UsuarioController(UsuarioService usuarioService, TicketService ticketService) {
        this.usuarioService = usuarioService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(this.usuarioService.getAllUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(this.usuarioService.getUsuarioById(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody @Valid RequestCrearUsuarioDTO payload) {
        return ResponseEntity.status(201).body(this.usuarioService.crearUsuario(payload));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuarioPatch(@PathVariable Long id) {
        return ResponseEntity.ok(this.usuarioService.desactivarUsuario(id));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuarioPut(@PathVariable Long id) {
        return ResponseEntity.ok(this.usuarioService.desactivarUsuario(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> desactivarUsuarioDelete(@PathVariable Long id) {
        return ResponseEntity.ok(this.usuarioService.desactivarUsuario(id));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Usuario> activarUsuarioPatch(@PathVariable Long id) {
        return ResponseEntity.ok(this.usuarioService.activarUsuario(id));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Usuario> activarUsuarioPut(@PathVariable Long id) {
        return ResponseEntity.ok(this.usuarioService.activarUsuario(id));
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<Ticket>> obtenerTicketsUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(this.ticketService.obtenerTicketsUsuario(id));
    }
}
