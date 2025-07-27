package com.fernandez.literalura.principal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fernandez.literalura.RespuestaConvertida;
import com.fernandez.literalura.modelos.Autor;
import com.fernandez.literalura.modelos.DatosLibro;
import com.fernandez.literalura.modelos.Libro;
import com.fernandez.literalura.servicio.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner sc = new Scanner(System.in);
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }


    public void muestraElMenu() throws IOException {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("banner.txt");
            String banner = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            System.out.println(ConsolaColor.AZUL +banner);
        } catch (Exception e) {
            System.out.println("Bienvenido al buscador de libros");
        }
        var opcion = -1;
        while (opcion != 0) {
            var menu = ConsolaColor.VERDE + "\n\n¿Qué deseas hacer?\n" + ConsolaColor.RESET + ConsolaColor.AZUL +
                    """
                            
                            1 - Buscar un libro para ingresarlo a tu biblioteca
                            2 - Listar tus libros registrados
                            3 - Listar autores registrados
                            4 - Listar autores vivos en un determinado año
                            5 - Listar libros por idioma
                            0 - Salir
                            """;
            System.out.println(menu);

            String entrada = sc.nextLine();
            if (entrada.isEmpty()) {
                System.out.println(ConsolaColor.ROJO + "❌ NO INGRESASTE NADA, POR FAVOR INGRESA UN NÚMERO DEL 0 AL 5." + ConsolaColor.AZUL);
                continue;
            }

            try {
                opcion = Integer.parseInt(entrada);

                switch (opcion) {
                    case 1 -> buscarLibro();
                    case 2 -> listarLibrosRegistrados();
                    case 3 -> listarAutoresRegistrados();
                    case 4 -> listarAutoresVivos();
                    case 5 -> listarLibrosPorIdioma();
                    case 0 -> {
                        System.out.println(ConsolaColor.VERDE + "¡Hasta pronto!\nSaliendo de la aplicación...\n " + ConsolaColor.RESET);
                        return;
                    }
                    default ->
                            System.out.println(ConsolaColor.ROJO + "❌️ POR FAVOR SELECCIONA UN NÚMERO DEL 0 AL 5." + ConsolaColor.AZUL);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsolaColor.ROJO + "❌ ENTRADA NO VÁLIDA, DEBES ESCRIBIR UN NÚMERO." + ConsolaColor.AZUL);
            }
        }
    }

    @Transactional
    private void buscarLibro() {
        while (true) {
            System.out.println(ConsolaColor.VERDE + "¿Qué libro buscas?        Menú Principal: 0" + ConsolaColor.AZUL);
            var libroBuscado = sc.nextLine();
            if (libroBuscado.isEmpty()) {
                System.out.println(ConsolaColor.ROJO + "❌ NO INGRESASTE NADA, POR FAVOR INGRESA EL NOMBRE DEL LIBRO." + ConsolaColor.AZUL);
                continue;
            }
            if (libroBuscado.equals("0")) {
                System.out.println(ConsolaColor.VERDE + "Saliendo de búsqueda de libros..." + ConsolaColor.AZUL);
                break;
            }
            String terminoCodificado = URLEncoder.encode(libroBuscado, StandardCharsets.UTF_8);
            var respuestaJson = consumoApi.obtenerDatosAPI(URL_BASE + terminoCodificado);
            var respuestaConvertida = conversor.obtenerDatos(respuestaJson, RespuestaConvertida.class);
            var numeroDeResultados = conversor.obtenerDatos(respuestaJson, CantidadDeResultados.class);
            if (respuestaConvertida.resultados().isEmpty()) {
                System.out.println(ConsolaColor.ROJO + "\n❌ ÉSE LIBRO NO SE ENCUENTRA EN PROJECT GUTENBERG EBOOK" + ConsolaColor.VERDE + "\n¿Quieres intentarlo con otro?" + ConsolaColor.AZUL);
            } else {
                System.out.println(ConsolaColor.VERDE + "\nLIBROS ENCONTRADOS: " + numeroDeResultados.cantidadDeResultados + (numeroDeResultados.cantidadDeResultados > 32 ? ". Se muestran 32. Puedes refinar tus resultados agregando más datos a tu búsqueda" : "") + ConsolaColor.AZUL);

                System.out.printf("| %-5s | %-80s | %-30s | %-15s | %-7s |%n", "No.", "Título", "Autor", "Idioma", "Descargas");
                System.out.println("|" + "-".repeat(7) + "|" + "-".repeat(82) + "|" + "-".repeat(32) + "|" + "-".repeat(17) + "|" + "-".repeat(11) + "|");

                for (DatosLibro libro : respuestaConvertida.resultados()) {
                    String nombreAutor = libro.autores().isEmpty() || libro.autores().stream()
                            .allMatch(a -> a.nombre().equalsIgnoreCase("anonymous") || a.nombre().equalsIgnoreCase("unknown"))
                            ? "Desconocido"
                            : libro.autores().stream()
                            .map(autor -> {
                                String[] partes = autor.nombre().split(",\\s+");
                                return partes.length > 1
                                        ? partes[1] + " " + partes[0]
                                        : autor.nombre();
                            })
                            .collect(Collectors.joining("/ "));

                    String idiomas = libro.idiomas().stream()
                            .map(this::obtenerNombreIdioma)
                            .collect(Collectors.joining(", "));

                    String numero = String.valueOf(respuestaConvertida.resultados().indexOf(libro) + 1);

                    System.out.printf("| %-5s | %-80s | %-30s | %-15s | %-9s |%n", numero, libro.titulo(), nombreAutor, idiomas, libro.numeroDeDescargas());
                }
                System.out.println(ConsolaColor.VERDE + "\nIngresa el número del libro que quieres guardar.      o 0 para volver a buscar" + ConsolaColor.AZUL);

                while (true) {

                    String entrada= sc.nextLine().trim();

                    if (entrada.isEmpty()) {
                        System.out.println(ConsolaColor.ROJO + "❌ NO INGRESASTE NADA, POR FAVOR INGRESA EL NÚMERO DEL LIBRO." + ConsolaColor.AZUL);
                        continue;
                    }
                    if (entrada.equals("0")) {
                        break;
                    }

                    try {
                        var seleccion = Integer.parseInt(entrada) - 1;


                    if (seleccion != -1 && seleccion <= (respuestaConvertida.resultados().toArray().length) - 1 && seleccion >= 0) {

                        var libroSeleccionado = respuestaConvertida.resultados().get(seleccion);

                        Optional<Libro> libroExistente = libroRepository.findByTitulo(libroSeleccionado.titulo());
                        if (libroExistente.isPresent()) {
                            System.out.println(ConsolaColor.ROJO + "❌ EL LIBRO YA ESTÁ EN TU BIBLIOTECA" + ConsolaColor.AZUL);
                            continue;
                        }

                        List<Autor> autores = libroSeleccionado.autores().stream()
                                .map(datosAutor -> {
                                    Optional<Autor> autorExistente = autorRepository.findByNombre(datosAutor.nombre());
                                    if (autorExistente.isPresent()) {
                                        return autorExistente.get();
                                    } else {
                                        Autor nuevoAutor = new Autor(datosAutor);
                                        return autorRepository.save(nuevoAutor);
                                    }
                                })
                                .collect(Collectors.toList());

                        Libro libroGuardar = new Libro(libroSeleccionado);
                        libroGuardar.setAutores(autores);
                        libroRepository.save(libroGuardar);

                        System.out.println(ConsolaColor.VERDE + "✅ ¡LISTO! EL LIBRO '" + libroGuardar.getTitulo().toUpperCase() + "' QUEDÓ GUARDADO EN TU BIBLIOTECA.\n¿Quieres guardar otro de ésta lista? ingresa el número.      o 0 para volver a buscar" + ConsolaColor.AZUL);
                        System.out.flush();
                    } else if (seleccion == -1) {
                        break;
                    } else {
                        System.out.println(ConsolaColor.ROJO + "❌ POR FAVOR SELECCIONA UN LIBRO POR SU NUMERO EN LA LISTA" + ConsolaColor.RESET);
                    }
                    } catch (NumberFormatException e) {
                        System.out.println(ConsolaColor.ROJO + "❌ ENTRADA NO VÁLIDA, DEBES ESCRIBIR UN NÚMERO." + ConsolaColor.AZUL);
                    }
                }
            }
        }
    }

    @Transactional
    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        System.out.println(ConsolaColor.VERDE + "LIBROS REGISTRADOS:" + ConsolaColor.AZUL);
        System.out.printf(ConsolaColor.VERDE + "| %-80s | %-30s | %-15s | %-7s |%n", "Título", "Autor", "Idioma", "Descargas" + ConsolaColor.AZUL);
        System.out.println("|" + "-".repeat(82) + "|" + "-".repeat(32) + "|" + "-".repeat(17) + "|" + "-".repeat(11) + "|");

        for (Libro libro : libros) {
            String nombreAutor = libro.getAutores().isEmpty() || libro.getAutores().stream()
                    .allMatch(a -> a.getNombre().equalsIgnoreCase("anonymous") || a.getNombre().equalsIgnoreCase("unknown"))
                    ? "Desconocido"
                    : libro.getAutores().stream()
                    .map(autor -> {
                        String[] partes = autor.getNombre().split(",\\s+");
                        return partes.length > 1
                                ? partes[1] + " " + partes[0]
                                : autor.getNombre();
                    })
                    .collect(Collectors.joining(" / "));

            String idiomas = libro.getIdiomas().stream()
                    .map(this::obtenerNombreIdioma)
                    .collect(Collectors.joining(", "));

            System.out.printf("| %-80s | %-30s | %-15s | %-9s |%n", libro.getTitulo(), nombreAutor, idiomas, libro.getNumeroDeDescargas());
        }


    }

    @Transactional
    private void listarAutoresRegistrados() {
        List<Autor> autoresRegistrados = autorRepository.findAll();
        System.out.println(ConsolaColor.VERDE + "AUTORES REGISTRADOS:" + ConsolaColor.AZUL);
        System.out.printf("| %-30s | %-11s | %-11s | %-80s |%n", "Nombre", "Nació", "Murió", "Libro");
        System.out.println("|" + "-".repeat(32) + "|" + "-".repeat(13) + "|" + "-".repeat(13) + "|" + "-".repeat(82) + "|");
        for (Autor autor : autoresRegistrados) {
            if (!autor.getNombre().equalsIgnoreCase("anonymous") && !autor.getNombre().equalsIgnoreCase("unknown")) {
                String nombreNormalizado = "";
                String[] partes = autor.getNombre().split(",\\s*");
                if (partes.length < 2) {
                    nombreNormalizado = autor.getNombre();
                } else {
                    nombreNormalizado = partes[1] + " " + partes[0];
                }


                Object nacimiento;
                Object muerte;
                if (autor.getFechaDeNacimiento() == 0) {
                    nacimiento = "Desconocido";
                } else {
                    nacimiento = String.valueOf(autor.getFechaDeNacimiento());
                }
                if (autor.getFechaDeMuerte() == 0) {
                    muerte = "Desconocido";
                } else {
                    muerte = String.valueOf(autor.getFechaDeMuerte());
                }
                System.out.printf("| %-30s | %-11s | %-11s | %-80s |%n", nombreNormalizado, nacimiento, muerte, autor.getLibros().stream()
                        .map(Libro::getTitulo).collect(Collectors.joining(", ")));
            }
        }
    }

    @Transactional
    private void listarAutoresVivos() {
        System.out.println(ConsolaColor.VERDE + "Escribe el año en el que quieras saber qué autores estaban vivos:      Salir de autores vivos: 0" + ConsolaColor.AZUL);
        while (true) {
            String entrada= sc.nextLine().trim();

            if (entrada.isEmpty()) {
                System.out.println(ConsolaColor.ROJO + "❌ NO INGRESASTE NADA, POR FAVOR INGRESA EL AÑO.      " + ConsolaColor.VERDE + "Salir de autores vivos: 0" + ConsolaColor.AZUL);
                continue;
            }
            if (entrada.equals("0")) {
                System.out.println(ConsolaColor.VERDE + "Saliendo de búsqueda por autores vivos..." + ConsolaColor.RESET);
                break;
            }

            try {
                int anio = Integer.parseInt(entrada);

                List<Autor> autoresVivos = autorRepository.findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(anio, anio);
                if (autoresVivos.isEmpty()) {
                    System.out.println(ConsolaColor.ROJO + "❌ NO TIENES AUTORES VIVOS EN ESE AÑO EN TU BIBLIOTECA" + ConsolaColor.VERDE + "\n¿Quieres buscar en otro año? ¿en cuál?      " + ConsolaColor.VERDE + "Salir de autores vivos: 0" + ConsolaColor.AZUL);
                    continue;
                }

                System.out.println(ConsolaColor.VERDE + "\nAUTORES VIVOS EN EL AÑO " + anio + ":" + ConsolaColor.AZUL);
                System.out.printf(ConsolaColor.VERDE +"| %-30s | %-11s | %-11s | %-80s |%n", "Nombre", "Nació", "Murió", "Libro" + ConsolaColor.AZUL);
                System.out.println("|" + "-".repeat(32) + "|" + "-".repeat(13) + "|" + "-".repeat(13) + "|" + "-".repeat(82) + "|");
                for (Autor autor : autoresVivos) {
                    String nombreNormalizado = "";
                    if (!autor.getNombre().equalsIgnoreCase("anonymous") && !autor.getNombre().equalsIgnoreCase("unknown")) {
                        String[] partes = autor.getNombre().split(",\\s*");
                        if (partes.length < 2) {
                            nombreNormalizado = autor.getNombre();
                        } else {
                            nombreNormalizado = partes[1] + " " + partes[0];
                        }
                    }

                    String nacimiento = autor.getFechaDeNacimiento() == 0 ? "Desconocido" : String.valueOf(autor.getFechaDeNacimiento());
                    String muerte = autor.getFechaDeMuerte() == 0 ? "Desconocido" : String.valueOf(autor.getFechaDeMuerte());

                    System.out.printf("| %-30s | %-11s | %-11s | %-80s |%n", nombreNormalizado, nacimiento, muerte, autor.getLibros().stream().map(Libro::getTitulo).collect(Collectors.joining(", ")));
                }
                System.out.println(ConsolaColor.VERDE +"\n¿Otro año?      " + "Salir de autores vivos: 0" + ConsolaColor.AZUL);
            } catch (NumberFormatException e) {
                System.out.println(ConsolaColor.ROJO + "❌ ENTRADA NO VÁLIDA, DEBES ESCRIBIR UN NÚMERO.      " + ConsolaColor.VERDE + "Salir de autores vivos: 0" + ConsolaColor.AZUL);
            }
        }
    }

    @Transactional
    private void listarLibrosPorIdioma() {
        System.out.println(ConsolaColor.VERDE + "Escribe el idioma que quieres buscar:      " + ConsolaColor.VERDE + "Salir de idiomas: 0" + ConsolaColor.AZUL);
        while (true) {
            String entrada = sc.nextLine().toLowerCase().trim();

            if (entrada.isEmpty()) {
                System.out.println(ConsolaColor.ROJO + "❌ NO INGRESASTE NADA, POR FAVOR INGRESA EL IDIOMA.      " + ConsolaColor.VERDE + "Salir de idiomas: 0" + ConsolaColor.AZUL);
                continue;
            }
            if (entrada.equals("0")) {
                System.out.println(ConsolaColor.VERDE + "Saliendo de búsqueda por idioma..." + ConsolaColor.RESET);
                break;
            }
            try {
                String idiomaBuscado = limpiarAcentos(entrada);
                String codigoIdioma = obtenerCodigoIdioma(idiomaBuscado);
                if (codigoIdioma == null) {
                    System.out.println(ConsolaColor.ROJO + "❌ IDIOMA NO RECONOCIDO" + ConsolaColor.VERDE + "  ¿Otro idioma?      " + "Salir de idiomas: 0)" + ConsolaColor.AZUL);
                    continue;
                }
                List<Libro> librosPorIdioma = libroRepository.findByIdioma(codigoIdioma);
                if (librosPorIdioma.isEmpty()) {
                    System.out.println(ConsolaColor.ROJO + "❌ NO TIENES LIBROS EN ESE IDIOMA EN TU BIBLIOTECA" + ConsolaColor.VERDE + "  ¿Otro idioma?      " + "\nSalir de idiomas: 0" + ConsolaColor.AZUL);
                    continue;
                }
                System.out.println(ConsolaColor.VERDE + "\nLIBROS EN " + librosPorIdioma.get(0).getIdiomas().stream()
                        .map(this::obtenerNombreIdioma)
                        .collect(Collectors.joining(", ")).toUpperCase() + ":" + ConsolaColor.AZUL);
                System.out.printf(ConsolaColor.VERDE + "| %-80s | %-30s | %-15s | %-9s |%n", "TÍTULO", "AUTOR", "IDIOMA", "DESCARGAS" + ConsolaColor.AZUL);
                System.out.println("|" + "-".repeat(82) + "|" + "-".repeat(32) + "|" + "-".repeat(17) + "|" + "-".repeat(11) + "|");

                for (Libro libro : librosPorIdioma) {
                    String nombreNormalizado = libro.getAutores().isEmpty() || libro.getAutores().stream()
                            .allMatch(a -> a.getNombre().equalsIgnoreCase("anonymous") || a.getNombre().equalsIgnoreCase("unknown"))
                            ? "Desconocido"
                            : libro.getAutores().stream()
                            .map(autor -> {
                                String[] partes = autor.getNombre().split(",\\s+");
                                return partes.length > 1
                                        ? partes[1] + " " + partes[0]
                                        : autor.getNombre();
                            })
                            .collect(Collectors.joining("/ "));

                    String idiomas = libro.getIdiomas().stream()
                            .map(this::obtenerNombreIdioma)
                            .collect(Collectors.joining(", "));
                    System.out.printf("| %-80s | %-30s | %-15s | %-9s |%n", libro.getTitulo(), nombreNormalizado, idiomas, libro.getNumeroDeDescargas());
                }
                System.out.println(ConsolaColor.VERDE +"\n¿Otro idioma?      " + "Salir de idiomas: 0" + ConsolaColor.AZUL);
            } catch (NumberFormatException e) {
                System.out.println(ConsolaColor.ROJO + "❌ ENTRADA NO VÁLIDA, DEBES ESCRIBIR SÓLO LETRAS." + ConsolaColor.VERDE + "  ¿Otro idioma?      Salir de idiomas: 0" + ConsolaColor.AZUL);
            }
        }
    }

    private String obtenerCodigoIdioma (String idiomaBuscado){
        var idiomaBuscadoLimpio = limpiarAcentos(idiomaBuscado.trim().toLowerCase());

        Map<String, String> idiomasPersonalizados = Map.of(
                "latin", "la"
        );

        if (idiomasPersonalizados.containsKey(idiomaBuscadoLimpio)) {
            return idiomasPersonalizados.get(idiomaBuscadoLimpio);
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            String nombre = limpiarAcentos(locale.getDisplayLanguage(new Locale("es")).toLowerCase());
            if (idiomaBuscadoLimpio.equals(nombre)) {
                return locale.getLanguage();
            }
        }
        return null;
    }


    private String obtenerNombreIdioma (String codigoIdioma){
        try {
            String codigoLimpio = codigoIdioma.replaceAll("[\\[\\]\\s]", "").toLowerCase();

            Locale locale = new Locale(codigoLimpio);

            return locale.getDisplayLanguage(new Locale("es", "ES"));
        } catch (Exception e) {
            return codigoIdioma;
        }
    }

    private String limpiarAcentos(String texto) {
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return textoNormalizado.replaceAll("\\p{M}", "");
    }



    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CantidadDeResultados(
            @JsonAlias("count") int cantidadDeResultados
    ){}
}


