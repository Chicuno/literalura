package com.fernandez.literalura.servicio;

import com.fernandez.literalura.modelos.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibroRepository extends JpaRepository <Libro, Long>{
    Optional<Libro> findByTituloContainsIgnoreCase(String nombreLibro);
}
