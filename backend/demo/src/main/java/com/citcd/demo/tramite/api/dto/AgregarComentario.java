package com.citcd.demo.tramite.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgregarComentario(@NotBlank @Size(max = 500) String comentario) {

}
