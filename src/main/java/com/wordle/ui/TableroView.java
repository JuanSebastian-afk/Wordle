package com.wordle.ui;

import com.wordle.model.EstadoLetra;
import com.wordle.model.ResultadoIntento;
import com.wordle.service.JuegoService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Vista principal del tablero 6x5 de Wordle en JavaFX.
 */
public class TableroView extends BorderPane {

    private final JuegoService juegoService;
    private final Label[][] celdas;
    private final StringBuilder bufferActual;

    private Label lblSubtitulo;
    private Label lblMensaje;
    private GridPane gridTablero;
    private Button btnBorrar;
    private Button btnEnviar;
    private Button btnNuevoJuego;

    public TableroView(JuegoService juegoService) {
        this.juegoService = juegoService;
        this.celdas = new Label[JuegoService.MAX_INTENTOS][JuegoService.LONGITUD_PALABRA];
        this.bufferActual = new StringBuilder();

        getStyleClass().add("root-container");
        construirInterfaz();
        configurarTeclado();
    }

    private void construirInterfaz() {
        // --- ENCABEZADO ---
        Label lblTitulo = new Label("WORDLE");
        lblTitulo.getStyleClass().add("app-title");

        lblSubtitulo = new Label("Intento 1 de " + JuegoService.MAX_INTENTOS);
        lblSubtitulo.getStyleClass().add("app-subtitle");

        VBox headerBox = new VBox(4, lblTitulo, lblSubtitulo);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getStyleClass().add("header-box");

        // --- BANNER DE MENSAJES ---
        lblMensaje = new Label("Escribe de 1 a 5 letras y presiona Enter");
        lblMensaje.getStyleClass().addAll("status-banner", "status-info");

        VBox topWrapper = new VBox(14, headerBox, lblMensaje);
        topWrapper.setAlignment(Pos.CENTER);
        topWrapper.setPadding(new Insets(18, 20, 8, 20));
        setTop(topWrapper);

        // --- TABLERO 6x5 ---
        gridTablero = new GridPane();
        gridTablero.setAlignment(Pos.CENTER);
        gridTablero.setHgap(8);
        gridTablero.setVgap(8);
        gridTablero.getStyleClass().add("board-grid");

        for (int fila = 0; fila < JuegoService.MAX_INTENTOS; fila++) {
            for (int col = 0; col < JuegoService.LONGITUD_PALABRA; col++) {
                Label celda = new Label("");
                celda.setAlignment(Pos.CENTER);
                celda.getStyleClass().addAll("tile", "tile-empty");
                celdas[fila][col] = celda;
                gridTablero.add(celda, col, fila);
            }
        }

        VBox centerWrapper = new VBox(gridTablero);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(10, 20, 10, 20));
        setCenter(centerWrapper);

        // --- CONTROLES INFERIORES ---
        btnBorrar = new Button("⌫ Borrar");
        btnBorrar.getStyleClass().addAll("btn", "btn-secondary");
        btnBorrar.setFocusTraversable(false);
        btnBorrar.setOnAction(e -> {
            borrarLetra();
            requestFocus();
        });

        btnEnviar = new Button("Enviar");
        btnEnviar.getStyleClass().addAll("btn", "btn-primary");
        btnEnviar.setFocusTraversable(false);
        btnEnviar.setOnAction(e -> {
            enviarIntento();
            requestFocus();
        });

        btnNuevoJuego = new Button("Nuevo juego");
        btnNuevoJuego.getStyleClass().addAll("btn", "btn-accent");
        btnNuevoJuego.setFocusTraversable(false);
        btnNuevoJuego.setOnAction(e -> {
            reiniciarPartida();
            requestFocus();
        });

        HBox botonesBox = new HBox(10, btnBorrar, btnEnviar, btnNuevoJuego);
        botonesBox.setAlignment(Pos.CENTER);

        Label lblAyuda = new Label("Teclado físico: A–Z escribir · Backspace borrar · Enter confirmar");
        lblAyuda.getStyleClass().add("helper-text");

        VBox bottomWrapper = new VBox(12, botonesBox, lblAyuda);
        bottomWrapper.setAlignment(Pos.CENTER);
        bottomWrapper.setPadding(new Insets(8, 20, 22, 20));
        setBottom(bottomWrapper);
    }

    private void configurarTeclado() {
        setFocusTraversable(true);
        setOnKeyPressed(this::manejarTecla);
    }

    private void manejarTecla(KeyEvent event) {
        if (juegoService.isJuegoTerminado()) {
            if (event.getCode() == KeyCode.ENTER) {
                reiniciarPartida();
            }
            return;
        }

        KeyCode code = event.getCode();
        if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
            borrarLetra();
            event.consume();
            return;
        }

        if (code == KeyCode.ENTER) {
            enviarIntento();
            event.consume();
            return;
        }

        String texto = event.getText();
        if (texto != null && texto.length() == 1) {
            String letra = texto.toUpperCase();
            if (letra.matches("[A-ZÑ]")) {
                agregarLetra(letra.charAt(0));
                event.consume();
            }
        }
    }

    private void agregarLetra(char letra) {
        if (juegoService.isJuegoTerminado() || bufferActual.length() >= JuegoService.LONGITUD_PALABRA) {
            return;
        }
        int fila = juegoService.getIntentosRealizados();
        int col = bufferActual.length();

        bufferActual.append(letra);
        Label celda = celdas[fila][col];
        celda.setText(String.valueOf(letra));
        aplicarEstiloCelda(celda, "tile-filled");
        animarPop(celda);
    }

    private void borrarLetra() {
        if (juegoService.isJuegoTerminado() || bufferActual.isEmpty()) {
            return;
        }
        int fila = juegoService.getIntentosRealizados();
        int col = bufferActual.length() - 1;

        bufferActual.deleteCharAt(col);
        Label celda = celdas[fila][col];
        celda.setText("");
        aplicarEstiloCelda(celda, "tile-empty");
    }

    private void enviarIntento() {
        if (juegoService.isJuegoTerminado()) {
            return;
        }

        int filaActual = juegoService.getIntentosRealizados();
        try {
            ResultadoIntento resultado = juegoService.evaluarIntento(bufferActual.toString());
            pintarFilaEvaluada(filaActual, resultado);
            bufferActual.setLength(0);

            if (juegoService.isVictoria()) {
                int intentos = juegoService.getIntentosRealizados();
                String textoIntentos = intentos == 1 ? "1 intento" : intentos + " intentos";
                mostrarMensaje("¡Excelente! Adivinaste la palabra en " + textoIntentos + ".", "status-success");
                lblSubtitulo.setText("Partida ganada (" + intentos + "/" + JuegoService.MAX_INTENTOS + ")");
                actualizarEstadoBotones(true);
            } else if (juegoService.isJuegoTerminado()) {
                mostrarMensaje("Sin intentos · La palabra secreta era: " + juegoService.getPalabraSecreta(), "status-error");
                lblSubtitulo.setText("Partida finalizada");
                actualizarEstadoBotones(true);
            } else {
                int siguiente = juegoService.getIntentosRealizados() + 1;
                lblSubtitulo.setText("Intento " + siguiente + " de " + JuegoService.MAX_INTENTOS);
                mostrarMensaje("Sigue intentando...", "status-info");
            }
        } catch (IllegalArgumentException ex) {
            mostrarMensaje(ex.getMessage(), "status-error");
            animarSacudidaFila(filaActual);
        }
    }

    private void pintarFilaEvaluada(int fila, ResultadoIntento resultado) {
        EstadoLetra[] estados = resultado.estados();
        for (int col = 0; col < JuegoService.LONGITUD_PALABRA; col++) {
            final Label celda = celdas[fila][col];
            if (estados[col] == EstadoLetra.VACIO) {
                aplicarEstiloCelda(celda, "tile-empty");
                continue;
            }

            final String claseEstado = switch (estados[col]) {
                case VERDE -> "tile-correct";
                case AMARILLO -> "tile-present";
                case GRIS -> "tile-absent";
                case VACIO -> "tile-empty";
            };

            PauseTransition pausa = new PauseTransition(Duration.millis(col * 85L));
            pausa.setOnFinished(e -> {
                aplicarEstiloCelda(celda, claseEstado);
                animarPop(celda);
            });
            pausa.play();
        }
    }

    private void reiniciarPartida() {
        juegoService.iniciarNuevaPartida();
        bufferActual.setLength(0);

        for (int fila = 0; fila < JuegoService.MAX_INTENTOS; fila++) {
            for (int col = 0; col < JuegoService.LONGITUD_PALABRA; col++) {
                Label celda = celdas[fila][col];
                celda.setText("");
                aplicarEstiloCelda(celda, "tile-empty");
            }
        }

        lblSubtitulo.setText("Intento 1 de " + JuegoService.MAX_INTENTOS);
        mostrarMensaje("Nueva palabra lista. ¡Mucha suerte!", "status-info");
        actualizarEstadoBotones(false);
    }

    private void actualizarEstadoBotones(boolean terminado) {
        btnBorrar.setDisable(terminado);
        btnEnviar.setDisable(terminado);
    }

    private void aplicarEstiloCelda(Label celda, String claseEstado) {
        celda.getStyleClass().removeAll("tile-empty", "tile-filled", "tile-correct", "tile-present", "tile-absent");
        celda.getStyleClass().add(claseEstado);
    }

    private void mostrarMensaje(String texto, String claseTipo) {
        lblMensaje.setText(texto);
        lblMensaje.getStyleClass().removeAll("status-info", "status-error", "status-success");
        lblMensaje.getStyleClass().add(claseTipo);
    }

    private void animarPop(Label celda) {
        ScaleTransition st = new ScaleTransition(Duration.millis(110), celda);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.08);
        st.setToY(1.08);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void animarSacudidaFila(int fila) {
        for (int col = 0; col < JuegoService.LONGITUD_PALABRA; col++) {
            Label celda = celdas[fila][col];
            TranslateTransition tt = new TranslateTransition(Duration.millis(55), celda);
            tt.setFromX(0);
            tt.setByX(6);
            tt.setAutoReverse(true);
            tt.setCycleCount(4);
            tt.play();
        }
    }
}
