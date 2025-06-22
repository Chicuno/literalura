package com.fernandez.literalura.principal;

import com.fernandez.literalura.RespuestaConvertida;
import com.fernandez.literalura.modelos.Autor;
import com.fernandez.literalura.modelos.Libro;
import com.fernandez.literalura.servicio.ConsumoAPI;
import com.fernandez.literalura.servicio.ConvierteDatos;
import com.fernandez.literalura.servicio.LibroRepository;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner sc = new Scanner(System.in);
    private LibroRepository repositorio;

    public Principal(LibroRepository libroRepository) {
        this.repositorio = libroRepository;
    }


    public void muestraElMenu() throws IOException {
        System.out.println("\nBienvenido al buscador de libros.");
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    ¿Qué deseas hacer?
                    
                    1 - Buscar un libro para ingresarlo a tu base de datos
                    2 - Listar tus libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("¡Hasta pronto! \nSaliendo de la aplicación...\n");
                    break;
                default:
                    System.out.println("Opción inválida, escoge una opción del 0 al 5");

            }
        }
    }


    private void buscarLibro() throws IOException {
        System.out.println("¿Qué libro buscas?");
        var libroBuscado = sc.nextLine();
        var respuestaJson = consumoApi.obtenerDatosAPI(URL_BASE + libroBuscado.replace(" ", "%20"));
        var respuestaConvertida = conversor.obtenerDatos(respuestaJson, RespuestaConvertida.class);
        if (respuestaConvertida.resultados().isEmpty()) {
            System.out.println("Ése libro no se encuentra en Project Gutenberg ebook");
        } else {
            System.out.println("\nLibros encontrados: " + respuestaConvertida.resultados().stream().count());

            for (int i = 0; i < respuestaConvertida.resultados().toArray().length; i++) {
                System.out.print(i + 1);
                System.out.print(" - Título: ");
                System.out.print(respuestaConvertida.resultados().get(i).titulo());
                System.out.print(". Autor: ");
                if (respuestaConvertida.resultados().get(i).autores() != null && !respuestaConvertida.resultados().get(i).autores().isEmpty()) {
                    System.out.print(respuestaConvertida.resultados().get(i).autores().stream().toList().get(0).nombre());
                } else {
                    System.out.println("no especificado");
                }
                System.out.print(". Idioma: ");
                respuestaConvertida.resultados().get(i).idiomas().forEach(System.out::println);
            }

            System.out.println("\nIngresa el número del libro que quieres guardar o 0 para volver al menú anterior");
            var libroSeleccionado = sc.nextInt() - 1;

            if (libroSeleccionado != -1) {

                var libroGuardar = respuestaConvertida.resultados().get(libroSeleccionado);

                Libro libro = new Libro(libroGuardar);

                List<Autor> autores = libroGuardar.autores().stream().map(datosAutor -> new Autor(datosAutor)).toList();

                libro.setAutores(autores);

                System.out.println(libro.getAutores().get(0).getNombre());

                repositorio.save(libro);
                System.out.println("¡Listo! el libro " + libro.getTitulo() + " quedó guardado en tu base de datos.");
            }
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = repositorio.findAll();
        System.out.println("Libros registrados:");
        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
    }

    private void listarAutoresVivos() {
    }

    private void listarLibrosPorIdioma() {
    }
}

