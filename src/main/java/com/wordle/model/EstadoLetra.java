package com.wordle.model;

/**
 * Representa el estado visual y lógico de cada letra evaluada en un intento.
 */
public enum EstadoLetra {
    VACIO,
    VERDE,     // Letra correcta en la posición correcta
    AMARILLO,  // Letra presente en la palabra, pero en otra posición
    GRIS       // Letra que no está en la palabra secreta
}
