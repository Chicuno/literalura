package com.fernandez.literalura.servicio;

import com.fernandez.literalura.modelos.Autor;
import com.fernandez.literalura.modelos.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository <Autor, Long> {
    Optional<Autor> findByNombreContainsIgnoreCase(String nombreAutor);

    List<Autor> findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(Integer anio, Integer anio1);

    Optional<Autor> findByNombre(String nombre);
}
