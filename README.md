# Wordle JavaFX (Java 21 + Maven)

Proyecto básico, estético y funcional inspirado en **Wordle**, desarrollado con **Java 21**, **JavaFX 21**, **Maven** y **JUnit 5**.

## Reglas configuradas

- **Palabras de 1 a 5 letras:** Puedes enviar intentos más cortos que las 5 casillas o cualquier combinación de letras sin bloquearse por el diccionario.
- **Evaluación por colores:** Verde (letra y posición correctas), Amarillo (letra presente en otra posición), Gris (letra ausente) y casillas sobrantes sin pintar.
- **6 intentos por partida** con opción de reiniciar en cualquier momento.

## Cómo abrir y ejecutar en IntelliJ IDEA

1. Descomprime `wordle-javafx-maven.zip`.
2. En **IntelliJ IDEA**, haz clic en **File -> Open...** y selecciona el archivo **`pom.xml`** (elige *Open as Project*).
3. Verifica en **File -> Project Structure -> Project** que esté seleccionado **JDK 21**.
4. Ejecuta la clase **`com.wordle.Launcher`** (botón verde *Run*) o desde la terminal:
   ```bash
   mvn clean javafx:run
   ```
5. Para correr las pruebas unitarias:
   ```bash
   mvn test
   ```
[INFO] Scanning for projects...
[INFO] Loaded 24473 auto-discovered prefixes for remote repository central (prefixes-central.txt)
[INFO] ------------------------------------------------------------------------
[ERROR] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.154 s
[INFO] Finished at: 2026-11-13T13:17:25-05:00
[INFO] ------------------------------------------------------------------------
[ERROR] Could not find goal 'runecho' in plugin org.openjfx:javafx-maven-plugin:0.0.8 among available goals jlink, run -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoNotFoundException
# Wordle
