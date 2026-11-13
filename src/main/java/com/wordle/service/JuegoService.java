package com.wordle.service;

import com.wordle.model.EstadoLetra;
import com.wordle.model.ResultadoIntento;

import java.util.HashMap;
import java.util.Map;

/**
 * Contiene las reglas principales del juego Wordle: selección de palabra secreta,
 * evaluación de intentos de 1 a 5 letras (sin restricción de diccionario) y
 * asignación de colores con manejo de letras repetidas.
 */
public class JuegoService {

    public static final int LONGITUD_PALABRA = 5;
    public static final int MAX_INTENTOS = 6;

    private final DiccionarioService diccionario;
    private String palabraSecreta;
    private int intentosRealizados;
    private boolean juegoTerminado;
    private boolean victoria;

    public JuegoService(DiccionarioService diccionario) {
        this.diccionario = diccionario;
        iniciarNuevaPartida();
    }

    /**
     * Constructor auxiliar útil para pruebas unitarias con palabra secreta controlada.
     */
    public JuegoService(DiccionarioService diccionario, String palabraSecretaFija) {
        this.diccionario = diccionario;
        configurarPartidaConPalabra(palabraSecretaFija);
    }

    public final void iniciarNuevaPartida() {
        String anterior = this.palabraSecreta;
        String nueva = diccionario.obtenerPalabraAleatoria();
        if (diccionario.getTotalPalabras() > 1) {
            while (nueva.equals(anterior)) {
                nueva = diccionario.obtenerPalabraAleatoria();
            }
        }
        configurarPartidaConPalabra(nueva);
    }

    public void configurarPartidaConPalabra(String palabra) {
        String normalizada = DiccionarioService.normalizar(palabra);
        if (!normalizada.matches("[A-ZÑ]{5}")) {
            throw new IllegalArgumentException("La palabra secreta debe tener exactamente 5 letras.");
        }
        this.palabraSecreta = normalizada;
        this.intentosRealizados = 0;
        this.juegoTerminado = false;
        this.victoria = false;
    }

    /**
     * Evalúa cualquier palabra de entre 1 y 5 letras sin exigir que exista en el diccionario.
     */
    public ResultadoIntento evaluarIntento(String intentoCrudo) {
        if (juegoTerminado) {
            throw new IllegalStateException("La partida ya ha terminado. Inicia un nuevo juego.");
        }

        String intento = DiccionarioService.normalizar(intentoCrudo);

        if (intento.isEmpty()) {
            throw new IllegalArgumentException("Escribe al menos 1 letra.");
        }
        if (intento.length() > LONGITUD_PALABRA) {
            throw new IllegalArgumentException("La palabra no puede tener más de 5 letras.");
        }
        if (!intento.matches("[A-ZÑ]{1,5}")) {
            throw new IllegalArgumentException("Solo se permiten letras de la A a la Z.");
        }

        EstadoLetra[] estados = calcularEstados(intento, palabraSecreta);
        intentosRealizados++;

        boolean acerto = intento.equals(palabraSecreta);
        if (acerto) {
            victoria = true;
            juegoTerminado = true;
        } else if (intentosRealizados >= MAX_INTENTOS) {
            juegoTerminado = true;
        }

        return new ResultadoIntento(intento, estados, acerto);
    }

    /**
     * Algoritmo de dos pasadas para calcular VERDE, AMARILLO y GRIS soportando
     * intentos más cortos de 5 letras (las casillas no usadas quedan en VACIO).
     */
    private EstadoLetra[] calcularEstados(String intento, String secreta) {
        EstadoLetra[] resultado = new EstadoLetra[LONGITUD_PALABRA];
        Map<Character, Integer> frecuenciaRestante = new HashMap<>();
        int longitudIntento = intento.length();

        // Primera pasada: coincidencias exactas (VERDE) y conteo de letras disponibles en la palabra secreta
        for (int i = 0; i < LONGITUD_PALABRA; i++) {
            char cSecreta = secreta.charAt(i);
            if (i < longitudIntento && intento.charAt(i) == cSecreta) {
                resultado[i] = EstadoLetra.VERDE;
            } else {
                frecuenciaRestante.put(cSecreta, frecuenciaRestante.getOrDefault(cSecreta, 0) + 1);
            }
        }

        // Segunda pasada: letras presentes en otra posición (AMARILLO) o ausentes (GRIS)
        for (int i = 0; i < LONGITUD_PALABRA; i++) {
            if (i >= longitudIntento) {
                resultado[i] = EstadoLetra.VACIO;
                continue;
            }
            if (resultado[i] == EstadoLetra.VERDE) {
                continue;
            }
            char cIntento = intento.charAt(i);
            int disponibles = frecuenciaRestante.getOrDefault(cIntento, 0);
            if (disponibles > 0) {
                resultado[i] = EstadoLetra.AMARILLO;
                frecuenciaRestante.put(cIntento, disponibles - 1);
            } else {
                resultado[i] = EstadoLetra.GRIS;
            }
        }

        return resultado;
    }

    public String getPalabraSecreta() {
        return palabraSecreta;
    }

    public int getIntentosRealizados() {
        return intentosRealizados;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public boolean isVictoria() {
        return victoria;
    }
}
