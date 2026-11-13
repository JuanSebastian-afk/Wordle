package com.wordle;

import com.wordle.service.DiccionarioService;
import com.wordle.service.JuegoService;
import com.wordle.ui.TableroView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Aplicación principal JavaFX de Wordle.
 */
public class WordleApp extends Application {

    @Override
    public void start(Stage stage) {
        DiccionarioService diccionarioService = new DiccionarioService();
        JuegoService juegoService = new JuegoService(diccionarioService);

        TableroView root = new TableroView(juegoService);
        Scene scene = new Scene(root, 480, 670);

        String css = Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Wordle · JavaFX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
