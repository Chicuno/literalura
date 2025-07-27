package com.fernandez.literalura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true)
    private String nombre;
    private int fechaDeNacimiento;
    private int fechaDeMuerte;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();

    public Autor() {}

    public Autor(DatosAutor a) {
        this.fechaDeMuerte = a.fechaDeMuerte();
        this.fechaDeNacimiento = a.fechaDeNacimiento();
        this.nombre = a.nombre();
    }

    public int getFechaDeMuerte() {return fechaDeMuerte;}
    public void setFechaDeMuerte(int fechaDeMuerte) {this.fechaDeMuerte = fechaDeMuerte;}

    public int getFechaDeNacimiento() {return fechaDeNacimiento;}
    public void setFechaDeNacimiento(int fechaDeNacimiento) {this.fechaDeNacimiento = fechaDeNacimiento;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public List<Libro> getLibros() {return libros;}
    public void setLibros(List<Libro> libros) {this.libros = libros;}

    public void agregarLibro(Libro libro) {
        if (libros == null) {
            libros = new ArrayList<>();
        }
        libros.add(libro);
    }
    public void setId(Long id) {
        Id = id;
    }

    @Override
    public String toString() {
        var normalizarNombre = this.getNombre().split(",\\s*");
        String nombreNormalizado = normalizarNombre[1] + " " + normalizarNombre[0];
        return  "Autor: " + nombreNormalizado + " " +
                "(" + fechaDeNacimiento +
                " - " + fechaDeMuerte + ")" +
                " Libro: " + libros.stream().map(libro -> libro.getTitulo()).toString();
    }
}

