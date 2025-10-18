package com.example.sudoku_express.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SudokuView:
 * Encargada de cargar y mostrar la vista FXML del Sudoku en pantalla completa.
 * No contiene lógica del juego; eso lo gestiona SudokuController.
 */
public class SudokuView {

    /**
     * Carga y muestra la vista del Sudoku en el Stage recibido.
     *
     * @param stage Stage principal o ventana actual donde se debe mostrar la vista.
     */
    public void show(Stage stage) {
        try {
            // Cargar la vista Sudoku y su controlador asociado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudoku_express/SudokuView.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Configurar propiedades visuales
            stage.setTitle("Sudoku 6x6 - Fundamentos de POOE");
            stage.setFullScreen(true);        // 🔹 Pantalla completa
            stage.setFullScreenExitHint("");  // 🔹 Sin texto “Presione ESC...”
            stage.setResizable(false);

            // Mostrar ventana
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ Error al cargar SudokuView.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
