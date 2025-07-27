package com.fernandez.literalura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private List<String> idiomas = new ArrayList<>();

    private int numeroDeDescargas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    public Libro() {
    }

    public Libro(DatosLibro datosLibro) {
        this.numeroDeDescargas = datosLibro.numeroDeDescargas();
        this.idiomas = datosLibro.idiomas();
        this.titulo = datosLibro.titulo();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public int getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(int numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        if (autores != null) {
            autores.forEach(a -> a.agregarLibro(this));
            this.autores = autores;
        }
    }

    @Override
    public String toString() {
        String idiomasStr = idiomas != null ? String.join(", ", idiomas) : "No especificado";

        String autoresStr = "Sin autores";
        if (autores != null && !autores.isEmpty()) {
            autoresStr = autores.stream()
                    .map(Autor::getNombre)
                    .collect(Collectors.joining(", "));
        }

        return  "  -" + titulo +
                ",  Idioma: " + idiomasStr +
                ",  Autor: " + autoresStr +
                ",  Número de descargas: " + numeroDeDescargas;
    }
}