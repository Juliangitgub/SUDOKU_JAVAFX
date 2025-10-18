package com.example.sudoku_express.Views;

import com.example.sudoku_express.Models.AlertBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainView: clase responsable de controlar la ventana principal y las transiciones
 * entre la vista de menú y la vista del juego (Sudoku).
 *
 * El controlador del menú delega en esta clase; MainView se encarga de:
 *  - recibir el Stage (ventana)
 *  - mostrar confirmación / mensajes si se desea
 *  - cargar la vista SudokuView.fxml en pantalla completa
 */
public class MainView {

    /**
     * Método invocado por MenuController para lanzar la vista del juego.
     * Carga SudokuView.fxml y la muestra en pantalla completa.
     *
     * @param stage Stage principal de la aplicación (obtenido desde el controlador del menú)
     */
    public void launchFromMenu(Stage stage) {
        try {
            // --- (2) Cargar la vista del Sudoku y mostrarla en pantalla completa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudoku_express/SudokuView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle("Sudoku 6x6 - Fundamentos POOE");
            stage.setFullScreen(true);           // mostrar en pantalla completa
            stage.setFullScreenExitHint("");     // quitar hint de salida
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            // En caso de error, mostrar alerta amigable
            AlertBox alertBox = new AlertBox();
            alertBox.showWarningAlertBox("Error", "No se pudo abrir la vista del Sudoku: " + e.getMessage(), "Error al cargar vista");
            e.printStackTrace();
        }
    }
}
