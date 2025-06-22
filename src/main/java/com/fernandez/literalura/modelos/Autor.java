package com.fernandez.literalura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "autores")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    //@Column(unique = true)
    @JsonAlias("name") private String nombre;
    @JsonAlias("birth_year") private int fechaDeNacimiento;
    @JsonAlias("death_year") private int fechaDeMuerte;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;

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

    public Libro getLibro() {return libro;}
    public void setLibro(Libro libro) {this.libro = libro;}

    public void setId(Long id) {
        Id = id;
    }

//    @Override
//    public String toString() {
//        return "Autor{" +
//                "Id=" + Id +
//                ", nombre='" + nombre + '\'' +
//                ", fechaDeNacimiento=" + fechaDeNacimiento +
//                ", fechaDeMuerte=" + fechaDeMuerte +
//                ", libro=" + libro +
//                '}';
//    }
}

