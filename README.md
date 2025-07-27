<h1 align="center"> Literalura </h1>
<h2 align="center"> Buscador de libros con la API Gutendex, que contiene los títulos de </h2>
<h2 align="center"> Project Gutenberg ebook </h2>

Al iniciar el programa te da la bienvenida y te da opciones para 

-Buscar un libro en Project Gutenberg ebook, seleccionar uno de la lista de resultados y agregarlo a tu base de datos,

-Listar tus libros registrados,

-Listar tus autores registrados,

-Ingresar un año para saber qué autores estaban vivos en ese año.

-Buscar dentro de la base de datos libros en un idioma determinado.

El programa incluye validaciones en cada una de las entradas del usuario para evitar que se ingresen caracteres no válidos 
según lo requiera el caso y evitar entradas vacías. Todas con sus respectivos mensajes. Normalización de las entradas para aceptar palabras con o sin acento, 
mayúsculas o minúsculas "n" o "ñ", así como aceptar el idioma buscado por su nombre en español, convirtiéndolo a su código de idioma 
para su búsqueda en la base de datos, así como la conversión de código de idioma a nombre en español para listar los libros, complementando con 
el mapeo para latín, que no está incluído en los idiomas que maneja Java. También se normalizan los nombres de los autores para mostrar el nombre primero y
después el apellido, ya que la respuesta de la api es primero apellido, luego nombre. También se codifican los caracteres especiales para evitar errores en
la búsqueda que se envía a la API en el caso de que el usuario los use.

En las opciones donde se pregunta otro dato para buscar, se tiene la opción de seguir buscando o regresar al menú principal.

En la opción de buscar para ingresar un libro a la base de datos, si los resultados son más de 32, se muestran 32 y se ofrece al usuario la opción de refinar los
resultados agregando palabras a la búsqueda. Si el libro que se selecciona ya está en la base de datos, no se permite volverlo a guardar, igualmente, si ya se tiene
guardado un autor y se guarda otro libro del mismo, no se guarda duplicado, sino que se relaciona el mismo con el nuevo libro. Después se ofrece la opción para
guardar otro libro de la lista de resultados o regresar a buscar otro. <br> <br>
<h2>Bienvenida</h2>
<img width="1366" height="768" alt="Bienvenida" src="https://github.com/user-attachments/assets/450cf7ee-0ee3-4b13-a3fd-0b8ec9c88989" /> <br> <br>

<h2>Entradas no válidas</h2>
<img width="1366" height="768" alt="Entradas no válidas" src="https://github.com/user-attachments/assets/6a165333-94ac-43cd-9068-124f432c88c1" /> <br> <br>

<h2>No encontrado, manejo de caracteres especiales y más de 32 resultados</h2>

<img width="1366" height="768" alt="No encontrado, manejo de caracteres especiales y más de 32 resultado" src="https://github.com/user-attachments/assets/19c0115e-af0b-47b4-a50a-7153c67054d8" /> <br> <br>

<h2>Guardando un libro duplicado</h2>
<img width="1366" height="556" alt="Guardando un libro duplicado" src="https://github.com/user-attachments/assets/ffc21839-2c1d-4cfa-8d22-bfc939454ddd" /> <br> <br>

<h2>Lista de libros registrados</h2>
<img width="1366" height="768" alt="Lista de libros registrados" src="https://github.com/user-attachments/assets/eadb8c02-5477-4e96-9e09-2fe358977bc6" /> <br> <br>

<h2>Lista de autores registrados</h2>
<img width="1366" height="768" alt="Lista de autores registrados" src="https://github.com/user-attachments/assets/99b52b40-9de1-4a7e-b813-9f1ab4498391" /> <br> <br>

<h2>Listar autores vivos en cierto año</h2>
<img width="1366" height="768" alt="Listar autores vivos en cierto año" src="https://github.com/user-attachments/assets/413d9e00-e028-4127-9044-c562b0052205" /> <br> <br>

<h2>Buscar libros por idioma</h2>
<img width="1366" height="768" alt="Buscar libros por idioma" src="https://github.com/user-attachments/assets/a899a3b4-2b3d-434b-a23b-a46e1cd43be0" /> <br> <br>

<h2>Salida</h2>
<img width="1366" height="768" alt="Salida" src="https://github.com/user-attachments/assets/b43c488e-e9f9-4c3c-af87-80b36ba57493" /> <br> <br>

<h4>Éste es una aplicación de consola, está hecha en Java. La compilación y ejecución se desarrolla en IntelliJ.</h4>

<h2>Tecnologías utilizadas</h2>

Java 17

Spring Boot 3.5.0

Maven 4.0.0

PostgreSQL

