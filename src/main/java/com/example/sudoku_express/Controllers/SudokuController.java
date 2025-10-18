package com.example.sudoku_express.Controllers;

import com.example.sudoku_express.Models.AlertBox;
import com.example.sudoku_express.Models.Board;
import com.example.sudoku_express.Models.Hint;
import com.example.sudoku_express.Models.HintSolver;
import com.example.sudoku_express.Models.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.Objects;

/**
 * Controlador del juego Sudoku (vista SudokuView.fxml).
 *
 * Cumple requisitos del miniproyecto:
 * - Eventos de mouse y teclado para seleccionar e ingresar números.
 * - Validación en tiempo real (fila, columna, bloque 2x3).
 * - Opción HELP ligada a HintSolver (una pista por uso).
 * - Reinicio con confirmación.
 *
 * Documentado con Javadoc.
 */
public class SudokuController {

    @FXML private GridPane sudokuGrid;
    @FXML private Button restartButton;
    @FXML private Button helpButton;

    // Modelo y utilidades
    private final Board board = Board.getInstance();    // Asegúrate de que Board sea singleton
    private final Validator validator = new Validator();
    private final HintSolver hintSolver = new HintSolver();
    private final AlertBox alertBox = new AlertBox();

    // Matriz de referencias a los TextField (36 celdas)
    private final TextField[][] cells = new TextField[6][6];

    // Selección actual
    private TextField selectedCell = null;
    private int selectedRow = -1;
    private int selectedCol = -1;

    /**
     * Inicialización llamada por JavaFX tras carga del FXML.
     * Enlaza celdas, configura listeners y carga estado del modelo.
     */
    @FXML
    public void initialize() {
        linkCellsFromGrid();
        loadModelToView();
        configureCellEvents();
        configureButtons();
    }

    /**
     * Busca en el GridPane los TextField por su row/columnIndex y los asigna a cells[row][col].
     * Protege contra índices nulos y fuera de rango.
     */
    private void linkCellsFromGrid() {
        sudokuGrid.getChildren().forEach(node -> {
            if (node instanceof TextField tf) {
                Integer row = GridPane.getRowIndex(tf);
                Integer col = GridPane.getColumnIndex(tf);
                int r = row == null ? 0 : row;
                int c = col == null ? 0 : col;
                if (r >= 0 && r < 6 && c >= 0 && c < 6) {
                    cells[r][c] = tf;
                }
            }
        });
    }

    /**
     * Carga el tablero actual desde el modelo (Board) hacia la vista.
     * Configura estilo para celdas fijas y editables.
     */
    private void loadModelToView() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                TextField tf = cells[r][c];
                if (tf == null) continue;

                int val = board.cellVGet(r, c);
                boolean isFixed = board.cellSGet(r, c);

                if (val != 0) tf.setText(String.valueOf(val));
                else tf.setText("");

                if (isFixed) {
                    tf.setDisable(true);
                    tf.setStyle(styleFixed());
                } else {
                    tf.setDisable(false);
                    tf.setStyle(styleNormal());
                }

                // Accessibility: prompt text as guide
                tf.setPromptText("0");
            }
        }
    }

    /**
     * Configura handlers de mouse y teclado para las celdas:
     * - Click para seleccionar.
     * - KeyTyped para ingresar 1-6.
     * - KeyPressed para Backspace/Delete.
     * - Validación en tiempo real antes de aplicar el número al modelo.
     */
    private void configureCellEvents() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                TextField tf = cells[r][c];
                if (tf == null) continue;

                final int row = r;
                final int col = c;

                // Click del mouse -> seleccionar
                tf.setOnMouseClicked(ev -> selectCell(row, col));

                // Teclas ingresadas (caracteres)
                tf.addEventHandler(KeyEvent.KEY_TYPED, ev -> {
                    if (tf.isDisabled()) {
                        ev.consume();
                        return;
                    }
                    String ch = ev.getCharacter();
                    if (ch == null || ch.isEmpty()) {
                        ev.consume();
                        return;
                    }

                    // Permitir solo '1'..'6'
                    if (ch.matches("[1-6]")) {
                        int number = Integer.parseInt(ch);

                        // Validación en tiempo real:
                        // temporalmente ponemos 0 en la celda del modelo para validar correctamente
                        int old = board.cellVGet(row, col);
                        board.cellMod(row, col, 0); // dejarla vacía mientras validamos
                        boolean valid = validator.isValidPlacement(row, col, number, board.getBoard(), 2, 3);
                        // aplicar según validación
                        board.cellMod(row, col, number);
                        // actualizar vista y estilo
                        Platform.runLater(() -> {
                            tf.setText(String.valueOf(number));
                            tf.setStyle(valid ? styleValid() : styleInvalid());
                        });
                        ev.consume();
                    } else {
                        // no permitido: consumir y evitar que aparezca
                        ev.consume();
                    }
                });

                // Teclas especiales (borrar)
                tf.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
                    if (tf.isDisabled()) {
                        if (ev.getCode() == KeyCode.BACK_SPACE || ev.getCode() == KeyCode.DELETE) {
                            ev.consume();
                        }
                        return;
                    }
                    if (ev.getCode() == KeyCode.BACK_SPACE || ev.getCode() == KeyCode.DELETE) {
                        board.cellMod(row, col, 0);
                        tf.setText("");
                        tf.setStyle(styleNormal());
                        ev.consume();
                    }
                });
            }
        }
    }

    /**
     * Configura los botones RESTART y HELP con sus acciones.
     */
    private void configureButtons() {
        restartButton.setOnAction(e -> confirmAndRestart());
        helpButton.setOnAction(e -> applyHelpHint());
    }

    /**
     * Selecciona la celda en (row,col) y la resalta visualmente.
     * @param row fila seleccionada
     * @param col columna seleccionada
     */
    private void selectCell(int row, int col) {
        TextField tf = cells[row][col];
        if (tf == null) return;

        // Des-resaltar anterior selección
        if (selectedCell != null && selectedCell != tf) {
            // si la celda anterior está vacía, devolver estilo normal
            int prevR = selectedRow;
            int prevC = selectedCol;
            if (prevR >= 0 && prevC >= 0 && cells[prevR][prevC] != null) {
                // si contiene un número, mantener su estilo de validación
                TextField prev = cells[prevR][prevC];
                String text = prev.getText();
                if (text != null && !text.isEmpty()) {
                    int v;
                    try { v = Integer.parseInt(text); }
                    catch (Exception ex) { v = 0; }
                    // validar célula actual si tiene número
                    if (v >= 1 && v <= 6) {
                        boolean valid = validator.isValidPlacement(prevR, prevC, v, board.getBoard(), 2, 3);
                        prev.setStyle(valid ? styleValid() : styleInvalid());
                    } else {
                        prev.setStyle(styleNormal());
                    }
                } else {
                    prev.setStyle(styleNormal());
                }
            }
        }

        selectedCell = tf;
        selectedRow = row;
        selectedCol = col;

        if (!tf.isDisabled()) tf.setStyle(styleSelected());
    }

    /**
     * CONFIRMACIÓN y reinicio del tablero.
     * Pide confirmación al usuario y regenera el puzzle en el modelo.
     */
    private void confirmAndRestart() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar reinicio");
        confirm.setHeaderText("Reiniciar tablero");
        confirm.setContentText("¿Estás seguro que deseas generar un nuevo tablero? Se perderán los avances actuales.");
        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            board.regenerateBoard();
            loadModelToView();
            alertBox.showAlertBox("Reinicio", "Se generó un nuevo tablero.", "Reinicio exitoso");
            // limpiar selección
            selectedCell = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    /**
     * Usa HintSolver.generarPista para obtener y aplicar una pista válida.
     * La pista se aplica solo a una celda vacía no fija. Se muestra un mensaje.
     */
    private void applyHelpHint() {
        Hint hint = hintSolver.generateHint(board.getBoard(), board.getFixed());
        if (hint == null) {
            alertBox.showWarningAlertBox("Ayuda", "No hay pistas disponibles en este tablero (o queda <=1 celda vacía).", "Sin pistas");
            return;
        }

        int r = hint.row;
        int c = hint.col;
        int value = hint.value;

        if (r < 0 || r >= 6 || c < 0 || c >= 6) {
            alertBox.showWarningAlertBox("Ayuda", "Pista inválida generada por el solver.", "Error");
            return;
        }

        TextField tf = cells[r][c];
        if (tf == null) {
            alertBox.showWarningAlertBox("Ayuda", "La celda sugerida no existe en la vista.", "Error");
            return;
        }

        // Aplicar la pista al modelo y a la vista (no la marcamos como fija)
        board.cellMod(r, c, value);
        tf.setText(String.valueOf(value));
        tf.setStyle(styleHint()); // resaltar pista
        alertBox.showAlertBox("Pista", String.format("Sugerencia: fila %d columna %d = %d", r + 1, c + 1, value), "Ayuda");
    }

    /* ---------------------- Estilos en línea (sin CSS externo) ---------------------- */

    private String styleNormal() {
        return "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold; -fx-border-color: #d2a679; -fx-border-radius: 5;";
    }

    private String styleSelected() {
        return "-fx-background-color: rgba(255,215,0,0.18); -fx-border-color: #ffd700; -fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold; -fx-border-radius: 5;";
    }

    private String styleFixed() {
        return "-fx-background-color: rgba(210,166,121,0.4); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #d2a679; -fx-border-radius: 5;";
    }

    private String styleValid() {
        return "-fx-background-color: rgba(0,255,0,0.14); -fx-border-color: #00ff00; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;";
    }

    private String styleInvalid() {
        return "-fx-background-color: rgba(255,0,0,0.12); -fx-border-color: #ff0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;";
    }

    private String styleHint() {
        return "-fx-background-color: rgba(0,200,255,0.14); -fx-border-color: #00bfff; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;";
    }
}



