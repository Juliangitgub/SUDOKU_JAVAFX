package com.example.sudoku_express.Controllers;

import com.example.sudoku_express.Models.AlertBox;
import com.example.sudoku_express.Views.MainView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Controlador del menú principal.
 * Ahora delega la apertura de la vista del juego a MainView.
 */
public class MenuController {

    /**
     * Evento que se ejecuta al presionar el botón "Iniciar".
     * Delegamos la transición a MainView para centralizar el flujo de la aplicación.
     */
    @FXML
    private void onIniciarClicked(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Mostrar un mensaje informativo rápido antes de delegar (opcional)
            AlertBox alertBox = new AlertBox();
            alertBox.showAlertBox(
                    "INICIO DE SUDOKU",
                    "ESTÁS A PUNTO DE INICIAR UN NUEVO JUEGO.",
                    "¡LISTO PARA COMENZAR!"
            );

            // Delegar la apertura/gestión de la vista principal a MainView
            MainView mainView = new MainView();
            mainView.launchFromMenu(stage);

        } catch (Exception e) {
            // Manejo simple de errores: muestra una alerta si algo falla
            AlertBox alertBox = new AlertBox();
            alertBox.showWarningAlertBox("Error", "No se pudo iniciar el juego: " + e.getMessage(), "Error al iniciar");
            e.printStackTrace();
        }
    }

    /**
     * Evento que se ejecuta al presionar el botón "Salir".
     */
    @FXML
    private void onSalirClicked(ActionEvent event) {
        System.exit(0);
    }
}
