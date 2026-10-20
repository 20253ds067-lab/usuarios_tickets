package com.example.demo;

import com.example.demo.controller.dto.RequestActualizarEstadoDTO;
import com.example.demo.controller.dto.RequestCrearTicketDTO;
import com.example.demo.controller.dto.RequestCrearUsuarioDTO;
import com.example.demo.controller.dto.RequestReasignarPrioridadDTO;
import com.example.demo.controller.ticket.TicketController;
import com.example.demo.controller.usuario.UsuarioController;
import com.example.demo.exception.customExceptions.BadRequestException;
import com.example.demo.model.repository.ticket.TicketRepository;
import com.example.demo.model.repository.usuario.UsuarioRepository;
import com.example.demo.model.ticket.EstadoTicket;
import com.example.demo.model.ticket.PrioridadTicket;
import com.example.demo.model.ticket.Ticket;
import com.example.demo.model.usuario.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class TicketControllerTests {

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private TicketController ticketController;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testCrearUsuarioYTicketController() {
        RequestCrearUsuarioDTO usuarioDto = new RequestCrearUsuarioDTO("Laura Perez");
        ResponseEntity<Usuario> respUsuario = usuarioController.crearUsuario(usuarioDto);
        Assertions.assertEquals(201, respUsuario.getStatusCode().value());
        Assertions.assertNotNull(respUsuario.getBody());
        Long usuarioId = respUsuario.getBody().getId();

        RequestCrearTicketDTO ticketDto = new RequestCrearTicketDTO();
        ticketDto.setDescripcion("Problema con VPN");
        ticketDto.setPrioridad("ALTA");
        ticketDto.setUsuarioId(usuarioId);

        ResponseEntity<Ticket> respTicket = ticketController.crearTicket(ticketDto);
        Assertions.assertEquals(201, respTicket.getStatusCode().value());
        Assertions.assertNotNull(respTicket.getBody());
        Assertions.assertEquals(EstadoTicket.ABIERTO, respTicket.getBody().getEstado());
        Assertions.assertEquals(PrioridadTicket.ALTA, respTicket.getBody().getPrioridad());
        Assertions.assertEquals("Problema con VPN", respTicket.getBody().getDescripcion());
    }

    @Test
    void testFlujoEstadosController() {
        RequestCrearUsuarioDTO usuarioDto = new RequestCrearUsuarioDTO("Carlos Ruiz");
        Usuario usuario = usuarioController.crearUsuario(usuarioDto).getBody();

        RequestCrearTicketDTO ticketDto = new RequestCrearTicketDTO();
        ticketDto.setDescripcion("Actualizacion de software");
        ticketDto.setPrioridad("MEDIA");
        ticketDto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketController.crearTicket(ticketDto).getBody();
        Assertions.assertEquals(EstadoTicket.ABIERTO, ticket.getEstado());

        ResponseEntity<Ticket> respEnProceso = ticketController.actualizarEstadoPatch(ticket.getId(), null, null);
        Assertions.assertEquals(200, respEnProceso.getStatusCode().value());
        Assertions.assertEquals(EstadoTicket.EN_PROCESO, respEnProceso.getBody().getEstado());

        RequestActualizarEstadoDTO cerradoDto = new RequestActualizarEstadoDTO("CERRADO");
        ResponseEntity<Ticket> respCerrado = ticketController.actualizarEstadoPut(ticket.getId(), cerradoDto, null);
        Assertions.assertEquals(200, respCerrado.getStatusCode().value());
        Assertions.assertEquals(EstadoTicket.CERRADO, respCerrado.getBody().getEstado());

        Assertions.assertThrows(BadRequestException.class, () -> {
            ticketController.actualizarEstadoPatch(ticket.getId(), null, null);
        });
    }

    @Test
    void testReasignarPrioridadController() {
        RequestCrearUsuarioDTO usuarioDto = new RequestCrearUsuarioDTO("Diana Ramos");
        Usuario usuario = usuarioController.crearUsuario(usuarioDto).getBody();

        RequestCrearTicketDTO ticketDto = new RequestCrearTicketDTO();
        ticketDto.setDescripcion("Ticket de red");
        ticketDto.setPrioridad("BAJA");
        ticketDto.setUsuarioId(usuario.getId());

        Ticket ticket = ticketController.crearTicket(ticketDto).getBody();
        Assertions.assertEquals(PrioridadTicket.BAJA, ticket.getPrioridad());

        RequestReasignarPrioridadDTO nuevaPrioridadDto = new RequestReasignarPrioridadDTO("ALTA");
        ResponseEntity<Ticket> resp = ticketController.reasignarPrioridadPatch(ticket.getId(), nuevaPrioridadDto, null);
        Assertions.assertEquals(200, resp.getStatusCode().value());
        Assertions.assertEquals(PrioridadTicket.ALTA, resp.getBody().getPrioridad());
    }

    @Test
    void testDesactivarUsuarioController() {
        RequestCrearUsuarioDTO usuarioDto = new RequestCrearUsuarioDTO("Sergio Vega");
        Usuario usuario = usuarioController.crearUsuario(usuarioDto).getBody();
        Assertions.assertTrue(usuario.isActivo());

        ResponseEntity<Usuario> respDesactivar = usuarioController.desactivarUsuarioPatch(usuario.getId());
        Assertions.assertEquals(200, respDesactivar.getStatusCode().value());
        Assertions.assertFalse(respDesactivar.getBody().isActivo());

        ResponseEntity<Usuario> respConsultar = usuarioController.getUsuarioById(usuario.getId());
        Assertions.assertEquals(200, respConsultar.getStatusCode().value());
        Assertions.assertFalse(respConsultar.getBody().isActivo());
    }

    @Test
    void testObtenerTicketsUsuarioController() {
        RequestCrearUsuarioDTO usuarioDto = new RequestCrearUsuarioDTO("Patricia Soto");
        Usuario usuario = usuarioController.crearUsuario(usuarioDto).getBody();

        RequestCrearTicketDTO ticketDto1 = new RequestCrearTicketDTO();
        ticketDto1.setDescripcion("Ticket 1");
        ticketDto1.setPrioridad("BAJA");
        ticketDto1.setUsuarioId(usuario.getId());
        ticketController.crearTicket(ticketDto1);

        RequestCrearTicketDTO ticketDto2 = new RequestCrearTicketDTO();
        ticketDto2.setDescripcion("Ticket 2");
        ticketDto2.setPrioridad("ALTA");
        ticketDto2.setUsuarioId(usuario.getId());
        ticketController.crearTicket(ticketDto2);

        ResponseEntity<List<Ticket>> resp1 = ticketController.obtenerTicketsUsuario(usuario.getId());
        Assertions.assertEquals(200, resp1.getStatusCode().value());
        Assertions.assertEquals(2, resp1.getBody().size());

        ResponseEntity<List<Ticket>> resp2 = usuarioController.obtenerTicketsUsuario(usuario.getId());
        Assertions.assertEquals(200, resp2.getStatusCode().value());
        Assertions.assertEquals(2, resp2.getBody().size());
    }
}
