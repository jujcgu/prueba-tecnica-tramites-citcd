package com.citcd.demo.tramite.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AsignarFuncionarioTramiteDTO(@NotNull @Positive Long funcionarioId) {

}
