package com.fernandez.literalura.servicio;

import com.fernandez.literalura.modelos.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository <Autor, Long> {
}
