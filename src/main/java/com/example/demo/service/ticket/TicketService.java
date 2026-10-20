package com.example.demo.service.ticket;

import com.example.demo.controller.dto.RequestCrearTicketDTO;
import com.example.demo.exception.customExceptions.BadRequestException;
import com.example.demo.model.repository.ticket.TicketRepository;
import com.example.demo.model.repository.usuario.UsuarioRepository;
import com.example.demo.model.ticket.EstadoTicket;
import com.example.demo.model.ticket.PrioridadTicket;
import com.example.demo.model.ticket.Ticket;
import com.example.demo.model.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    public TicketService(TicketRepository ticketRepository, UsuarioRepository usuarioRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }
        return ticketRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El ticket con id " + id + " no existe"));
    }

    @Transactional
    public Ticket crearTicket(RequestCrearTicketDTO payload) {
        if (payload.getDescripcion() == null || payload.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException("La descripción no puede estar vacía");
        }

        Long usuarioId = payload.getUsuarioIdEfectivo();
        if (usuarioId == null || usuarioId <= 0) {
            throw new BadRequestException("El id de usuario es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException("El usuario con id " + usuarioId + " no existe"));

        if (!usuario.isActivo()) {
            throw new BadRequestException("El usuario no está activo");
        }

        PrioridadTicket prioridad;
        try {
            prioridad = PrioridadTicket.valueOf(payload.getPrioridad().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("La prioridad debe ser BAJA, MEDIA o ALTA");
        }

        Ticket ticket = new Ticket();
        ticket.setDescripcion(payload.getDescripcion().trim());
        ticket.setEstado(EstadoTicket.ABIERTO);
        ticket.setPrioridad(prioridad);
        ticket.setUsuario(usuario);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket actualizarEstadoTicket(Long id, String nuevoEstadoStr) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El ticket con id " + id + " no existe"));

        if (ticket.getEstado() == EstadoTicket.CERRADO) {
            throw new BadRequestException("El ticket ya está CERRADO y no se puede modificar");
        }

        EstadoTicket estadoSiguiente;
        if (ticket.getEstado() == EstadoTicket.ABIERTO) {
            estadoSiguiente = EstadoTicket.EN_PROCESO;
        } else if (ticket.getEstado() == EstadoTicket.EN_PROCESO) {
            estadoSiguiente = EstadoTicket.CERRADO;
        } else {
            throw new BadRequestException("El ticket ya está CERRADO y no se puede modificar");
        }

        if (nuevoEstadoStr != null && !nuevoEstadoStr.isBlank()) {
            EstadoTicket estadoDeseado;
            try {
                estadoDeseado = EstadoTicket.valueOf(nuevoEstadoStr.toUpperCase());
            } catch (Exception e) {
                throw new BadRequestException("Estado inválido: " + nuevoEstadoStr);
            }

            if (estadoDeseado != estadoSiguiente) {
                throw new BadRequestException("Transición inválida: de " + ticket.getEstado() + " solo se puede pasar a " + estadoSiguiente);
            }
        }

        ticket.setEstado(estadoSiguiente);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket reasignarPrioridad(Long id, String nuevaPrioridadStr) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }

        if (nuevaPrioridadStr == null || nuevaPrioridadStr.isBlank()) {
            throw new BadRequestException("La prioridad es obligatoria");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El ticket con id " + id + " no existe"));

        if (ticket.getEstado() == EstadoTicket.CERRADO) {
            throw new BadRequestException("No se puede cambiar la prioridad de un ticket CERRADO");
        }

        PrioridadTicket prioridad;
        try {
            prioridad = PrioridadTicket.valueOf(nuevaPrioridadStr.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("La prioridad debe ser BAJA, MEDIA o ALTA");
        }

        ticket.setPrioridad(prioridad);
        return ticketRepository.save(ticket);
    }

    public List<Ticket> obtenerTicketsUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException("El usuario con id " + usuarioId + " no existe"));

        return ticketRepository.findByUsuarioId(usuario.getId());
    }
}
