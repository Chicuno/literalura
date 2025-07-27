package com.fernandez.literalura.servicio;

import com.fernandez.literalura.modelos.Autor;
import com.fernandez.literalura.modelos.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository <Libro, Long>{

    @Query("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.libros")
    List<Autor> findAllWithLibros();

    @Query(value = "SELECT * FROM libros WHERE :codigoIdioma = ANY(idiomas)", nativeQuery = true)
    List<Libro> findByIdioma(String codigoIdioma);
@Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autores")
    List<Libro> findAllWithAutores();

    Optional<Libro> findByTitulo(String titulo);

}


