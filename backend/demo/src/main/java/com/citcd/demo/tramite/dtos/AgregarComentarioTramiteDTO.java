package com.citcd.demo.tramite.dtos;

import jakarta.validation.constraints.Size;

public record AgregarComentarioTramiteDTO(@Size(max = 280) String comentario) {

}
