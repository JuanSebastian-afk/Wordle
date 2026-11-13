package com.wordle.service;

import com.wordle.model.EstadoLetra;
import com.wordle.model.ResultadoIntento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JuegoServiceTest {

    private DiccionarioService diccionario;

    @BeforeEach
    void setUp() {
        diccionario = new DiccionarioService();
    }

    @Test
    void evaluarIntentoExactoDebeGanarPartida() {
        JuegoService juego = new JuegoService(diccionario, "PLAYA");
        ResultadoIntento res = juego.evaluarIntento("PLAYA");

        assertTrue(res.esVictoria());
        assertTrue(juego.isVictoria());
        assertTrue(juego.isJuegoTerminado());
        assertArrayEquals(
                new EstadoLetra[]{EstadoLetra.VERDE, EstadoLetra.VERDE, EstadoLetra.VERDE, EstadoLetra.VERDE, EstadoLetra.VERDE},
                res.estados()
        );
    }

    @Test
    void evaluarLetrasRepetidasDebeAsignarColoresCorrectamente() {
        JuegoService juego = new JuegoService(diccionario, "PERRO");
        ResultadoIntento res = juego.evaluarIntento("ARROZ");

        assertFalse(res.esVictoria());
        assertArrayEquals(
                new EstadoLetra[]{EstadoLetra.GRIS, EstadoLetra.AMARILLO, EstadoLetra.VERDE, EstadoLetra.AMARILLO, EstadoLetra.GRIS},
                res.estados()
        );
    }

    @Test
    void evaluarPalabraFueraDeDiccionarioYPalabraCortaDebeFuncionar() {
        JuegoService juego = new JuegoService(diccionario, "PLAYA");

        // Palabra corta (3 letras) que no está en el diccionario
        ResultadoIntento resCorto = juego.evaluarIntento("PLA");
        assertFalse(resCorto.esVictoria());
        assertEquals(1, juego.getIntentosRealizados());
        assertArrayEquals(
                new EstadoLetra[]{EstadoLetra.VERDE, EstadoLetra.VERDE, EstadoLetra.VERDE, EstadoLetra.VACIO, EstadoLetra.VACIO},
                resCorto.estados()
        );

        // Combinación de 5 letras que no existe en el diccionario
        ResultadoIntento resLibre = juego.evaluarIntento("ZZZZY");
        assertFalse(resLibre.esVictoria());
        assertEquals(2, juego.getIntentosRealizados());
        assertArrayEquals(
                new EstadoLetra[]{EstadoLetra.GRIS, EstadoLetra.GRIS, EstadoLetra.GRIS, EstadoLetra.GRIS, EstadoLetra.AMARILLO},
                resLibre.estados()
        );
    }

    @Test
    void intentoVacioNoDebeConsumirTurno() {
        JuegoService juego = new JuegoService(diccionario, "MUNDO");

        assertThrows(IllegalArgumentException.class, () -> juego.evaluarIntento(""));
        assertEquals(0, juego.getIntentosRealizados());
        assertFalse(juego.isJuegoTerminado());
    }
}
