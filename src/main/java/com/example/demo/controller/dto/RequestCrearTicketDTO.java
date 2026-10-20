package com.example.demo.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCrearTicketDTO {

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotBlank(message = "La prioridad es obligatoria")
    @Pattern(
            regexp = "^(BAJA|MEDIA|ALTA)$",
            message = "La prioridad debe ser BAJA, MEDIA o ALTA"
    )
    private String prioridad;

    private Long usuarioId;

    private RequestUsuarioIdDTO usuario;

    public Long getUsuarioIdEfectivo() {
        if (usuarioId != null) {
            return usuarioId;
        }
        if (usuario != null) {
            return usuario.getId();
        }
        return null;
    }
}
