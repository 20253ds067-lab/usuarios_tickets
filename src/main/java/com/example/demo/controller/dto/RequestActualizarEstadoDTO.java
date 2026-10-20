package com.example.demo.controller.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestActualizarEstadoDTO {

    @Pattern(
            regexp = "^(ABIERTO|EN_PROCESO|CERRADO)$",
            message = "El estado debe ser ABIERTO, EN_PROCESO o CERRADO"
    )
    private String estado;
}
