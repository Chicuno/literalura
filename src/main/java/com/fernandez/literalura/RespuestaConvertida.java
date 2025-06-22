package com.fernandez.literalura;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fernandez.literalura.modelos.DatosLibro;
import com.fernandez.literalura.modelos.Libro;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RespuestaConvertida(
        @JsonAlias("results") List<DatosLibro> resultados
) {
}
