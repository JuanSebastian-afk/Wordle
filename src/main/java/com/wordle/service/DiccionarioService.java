package com.wordle.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Servicio encargado de cargar y proveer palabras secretas de 5 letras.
 */
public class DiccionarioService {

    private final List<String> listaPalabras;
    private final Set<String> conjuntoPalabras;
    private final Random random;

    public DiccionarioService() {
        this("/palabras.txt");
    }

    public DiccionarioService(String rutaRecurso) {
        this.random = new Random();
        Set<String> cargadas = cargarDesdeRecurso(rutaRecurso);

        if (cargadas.isEmpty()) {
            cargadas.addAll(List.of("PLAYA", "MUNDO", "LIBRO", "FUEGO", "CIELO", "VERDE", "TIGRE", "PIANO"));
        }

        this.conjuntoPalabras = Collections.unmodifiableSet(cargadas);
        this.listaPalabras = List.copyOf(new ArrayList<>(cargadas));
    }

    private Set<String> cargarDesdeRecurso(String rutaRecurso) {
        Set<String> palabras = new HashSet<>();
        InputStream is = getClass().getResourceAsStream(rutaRecurso);
        if (is == null) {
            return palabras;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String normalizada = normalizar(linea);
                if (normalizada.matches("[A-ZÑ]{5}")) {
                    palabras.add(normalizada);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo del diccionario: " + rutaRecurso, e);
        }
        return palabras;
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String limpio = texto.trim().toUpperCase();
        limpio = limpio.replace("Ñ", "__ENIE__");
        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return limpio.replace("__ENIE__", "Ñ");
    }

    public String obtenerPalabraAleatoria() {
        return listaPalabras.get(random.nextInt(listaPalabras.size()));
    }

    public boolean esPalabraValida(String palabra) {
        return conjuntoPalabras.contains(normalizar(palabra));
    }

    public int getTotalPalabras() {
        return listaPalabras.size();
    }
}
