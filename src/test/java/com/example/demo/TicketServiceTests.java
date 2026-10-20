package com.example.demo;

import com.example.demo.controller.dto.RequestCrearTicketDTO;
import com.example.demo.controller.dto.RequestCrearUsuarioDTO;
import com.example.demo.exception.customExceptions.BadRequestException;
import com.example.demo.model.repository.ticket.TicketRepository;
import com.example.demo.model.repository.usuario.UsuarioRepository;
import com.example.demo.model.ticket.EstadoTicket;
import com.example.demo.model.ticket.PrioridadTicket;
import com.example.demo.model.ticket.Ticket;
import com.example.demo.model.usuario.Usuario;
import com.example.demo.service.ticket.TicketService;
import com.example.demo.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TicketServiceTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testCrearTicketExitoso() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Carlos Perez"));

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Problema con conexion de red");
        dto.setPrioridad("ALTA");
        dto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketService.crearTicket(dto);

        assertNotNull(ticket.getId());
        assertEquals("Problema con conexion de red", ticket.getDescripcion());
        assertEquals(EstadoTicket.ABIERTO, ticket.getEstado());
        assertEquals(PrioridadTicket.ALTA, ticket.getPrioridad());
        assertEquals(usuario.getId(), ticket.getUsuario().getId());
    }

    @Test
    void testCrearTicketUsuarioInactivoFalla() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Maria Lopez"));
        usuarioService.desactivarUsuario(usuario.getId());

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Fallo en login");
        dto.setPrioridad("MEDIA");
        dto.setUsuarioId(usuario.getId());

        assertThrows(BadRequestException.class, () -> ticketService.crearTicket(dto));
    }

    @Test
    void testCrearTicketDescripcionVaciaFalla() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Juan Gomez"));

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("   ");
        dto.setPrioridad("BAJA");
        dto.setUsuarioId(usuario.getId());

        assertThrows(BadRequestException.class, () -> ticketService.crearTicket(dto));
    }

    @Test
    void testCrearTicketUsuarioNoExisteFalla() {
        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Error en sistema");
        dto.setPrioridad("ALTA");
        dto.setUsuarioId(99999L);

        assertThrows(BadRequestException.class, () -> ticketService.crearTicket(dto));
    }

    @Test
    void testActualizarEstadoTicketSecuencial() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Ana Torres"));

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Solicitud de acceso");
        dto.setPrioridad("BAJA");
        dto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketService.crearTicket(dto);
        assertEquals(EstadoTicket.ABIERTO, ticket.getEstado());

        Ticket ticketEnProceso = ticketService.actualizarEstadoTicket(ticket.getId(), null);
        assertEquals(EstadoTicket.EN_PROCESO, ticketEnProceso.getEstado());

        Ticket ticketCerrado = ticketService.actualizarEstadoTicket(ticket.getId(), "CERRADO");
        assertEquals(EstadoTicket.CERRADO, ticketCerrado.getEstado());

        assertThrows(BadRequestException.class, () -> ticketService.actualizarEstadoTicket(ticket.getId(), null));
    }

    @Test
    void testActualizarEstadoTicketSaltoInvalidoFalla() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Pedro Diaz"));

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Fallo general");
        dto.setPrioridad("MEDIA");
        dto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketService.crearTicket(dto);

        assertThrows(BadRequestException.class, () -> ticketService.actualizarEstadoTicket(ticket.getId(), "CERRADO"));
    }

    @Test
    void testReasignarPrioridadExitoso() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Luis Ramos"));

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Mantenimiento preventivo");
        dto.setPrioridad("BAJA");
        dto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketService.crearTicket(dto);
        assertEquals(PrioridadTicket.BAJA, ticket.getPrioridad());

        Ticket ticketActualizado = ticketService.reasignarPrioridad(ticket.getId(), "ALTA");
        assertEquals(PrioridadTicket.ALTA, ticketActualizado.getPrioridad());
    }

    @Test
    void testReasignarPrioridadTicketCerradoFalla() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Sofia Vega"));

        RequestCrearTicketDTO dto = new RequestCrearTicketDTO();
        dto.setDescripcion("Error en base de datos");
        dto.setPrioridad("MEDIA");
        dto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketService.crearTicket(dto);
        ticketService.actualizarEstadoTicket(ticket.getId(), null);
        ticketService.actualizarEstadoTicket(ticket.getId(), null);

        assertThrows(BadRequestException.class, () -> ticketService.reasignarPrioridad(ticket.getId(), "BAJA"));
    }

    @Test
    void testDesactivarUsuarioNoEliminaDatos() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Fernando Ruiz"));
        assertTrue(usuario.isActivo());

        Usuario desactivado = usuarioService.desactivarUsuario(usuario.getId());
        assertFalse(desactivado.isActivo());

        Usuario enBd = usuarioService.getUsuarioById(usuario.getId());
        assertNotNull(enBd);
        assertEquals("Fernando Ruiz", enBd.getNombre());
        assertFalse(enBd.isActivo());
    }

    @Test
    void testObtenerTicketsUsuario() {
        Usuario usuario = usuarioService.crearUsuario(new RequestCrearUsuarioDTO("Elena Garcia"));

        RequestCrearTicketDTO dto1 = new RequestCrearTicketDTO();
        dto1.setDescripcion("Ticket 1");
        dto1.setPrioridad("BAJA");
        dto1.setUsuarioId(usuario.getId());
        ticketService.crearTicket(dto1);

        RequestCrearTicketDTO dto2 = new RequestCrearTicketDTO();
        dto2.setDescripcion("Ticket 2");
        dto2.setPrioridad("ALTA");
        dto2.setUsuarioId(usuario.getId());
        ticketService.crearTicket(dto2);

        List<Ticket> tickets = ticketService.obtenerTicketsUsuario(usuario.getId());
        assertEquals(2, tickets.size());
    }
}
