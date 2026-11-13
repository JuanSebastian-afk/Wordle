package com.wordle.model;

/**
 * Resultado inmutable de evaluar una palabra ingresada por el jugador.
 */
public record ResultadoIntento(
        String palabra,
        EstadoLetra[] estados,
        boolean esVictoria
) {
}
