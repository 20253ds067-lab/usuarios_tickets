package com.example.demo.service.usuario;

import com.example.demo.controller.dto.RequestCrearUsuarioDTO;
import com.example.demo.exception.customExceptions.BadRequestException;
import com.example.demo.model.repository.usuario.UsuarioRepository;
import com.example.demo.model.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El usuario con id " + id + " no existe"));
    }

    @Transactional
    public Usuario crearUsuario(RequestCrearUsuarioDTO payload) {
        if (payload.getNombre() == null || payload.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(payload.getNombre().trim());
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario desactivarUsuario(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El usuario con id " + id + " no existe"));

        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario activarUsuario(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado es inválido");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El usuario con id " + id + " no existe"));

        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
}
