package com.example.sudoku_express.Controllers;

import com.example.sudoku_express.Models.AlertBox;
import com.example.sudoku_express.Models.Board;
import com.example.sudoku_express.Models.Hint;
import com.example.sudoku_express.Models.HintSolver;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class SudokuController {

    @FXML
    private GridPane sudokuGrid;

    @FXML
    private Button restartButton;

    @FXML
    private Button helpButton;

    // Modelo y utilidades
    private final Board board = new Board();        // si usas singleton, cámbialo a Board.getInstance()
    private final AlertBox alert = new AlertBox();
    private final HintSolver hintSolver = new HintSolver();

    private TextField[][] cells = new TextField[6][6];

    /**
     * Se ejecuta automáticamente al cargar la vista (FXML).
     */
    @FXML
    public void initialize() {
        // Vincular las celdas del FXML al arreglo 2D
        loadCellsFromGrid();

        // Cargar los valores iniciales del tablero generado
        fillBoardFromModel();

        // Asociar los eventos de los botones
        restartButton.setOnAction(e -> onRestartClicked());
        helpButton.setOnAction(e -> onHelpClicked());
    }

    /**
     * Lee los TextField del GridPane y los guarda en el arreglo cells.
     */
    private void loadCellsFromGrid() {
        sudokuGrid.getChildren().forEach(node -> {
            if (node instanceof TextField cell) {
                Integer col = GridPane.getColumnIndex(cell);
                Integer row = GridPane.getRowIndex(cell);
                if (col == null) col = 0;
                if (row == null) row = 0;
                // Protección por si el FXML está mal indexado
                if (row >= 0 && row < 6 && col >= 0 && col < 6) {
                    cells[row][col] = cell;
                }
            }
        });
    }

    /**
     * Muestra el tablero inicial desde el modelo Board.
     */
    private void fillBoardFromModel() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                int value = board.cellVGet(row, col);
                boolean isFixed = board.cellSGet(row, col);

                TextField cell = cells[row][col];
                if (cell == null) continue;

                if (value != 0) {
                    cell.setText(String.valueOf(value));
                } else {
                    cell.setText("");
                }

                // Si es fija, la bloqueamos visualmente
                if (isFixed) {
                    cell.setDisable(true);
                    cell.setStyle("-fx-background-color: rgba(210,166,121,0.4); "
                            + "-fx-text-fill: white; -fx-font-weight: bold; "
                            + "-fx-border-color: #d2a679; -fx-border-radius: 5;");
                } else {
                    cell.setDisable(false);
                    cell.setStyle("-fx-background-color: rgba(255,255,255,0.15); "
                            + "-fx-text-fill: white; -fx-font-size: 22px; "
                            + "-fx-font-weight: bold; -fx-border-color: #d2a679; "
                            + "-fx-border-radius: 5;");
                }

                // Escuchar cuando el usuario cambia un valor
                final int r = row;
                final int c = col;
                cell.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!cell.isDisabled()) {
                        if (newVal.matches("[1-6]")) {
                            board.cellMod(r, c, Integer.parseInt(newVal));
                        } else if (newVal.isEmpty()) {
                            board.cellMod(r, c, 0);
                        } else {
                            // evitar entradas inválidas
                            cell.setText("");
                        }
                    }
                });
            }
        }
    }

    /**
     * Reinicia el tablero con un nuevo puzzle.
     */
    private void onRestartClicked() {
        board.regenerateBoard();
        alert.showAlertBox("Nuevo Sudoku", "Se generó un nuevo tablero.", "Reinicio exitoso");
        fillBoardFromModel();
    }

    /**
     * Usa HintSolver para obtener una pista y aplicarla a la vista y al modelo.
     */
    private void onHelpClicked() {
        // Pedimos al solver una pista basada en el tablero y las celdas fijas
        Hint hint = hintSolver.generateHint(board.getBoard(), board.getFixed());

        if (hint == null) {
            alert.showWarningAlertBox("Pista", "No se pudo generar una pista en este tablero.", "Sin pistas disponibles");
            return;
        }

        int r = hint.row;
        int c = hint.col;
        int value = hint.value;

        // Seguridad: validar índices
        if (r < 0 || r >= 6 || c < 0 || c >= 6) {
            alert.showWarningAlertBox("Pista inválida", "Se generó una pista fuera de rango.", "Error de solver");
            return;
        }

        TextField target = cells[r][c];
        if (target == null) {
            alert.showWarningAlertBox("Pista", "La celda de la pista no se encuentra en la vista.", "Error");
            return;
        }

        // Aplica la pista: actualiza modelo y vista
        board.cellMod(r, c, value);
        target.setText(String.valueOf(value));

        // Estilo para pista (resaltar como correcta)
        target.setStyle("-fx-background-color: rgba(0,255,0,0.15); -fx-text-fill: white; -fx-border-color: #00ff00; -fx-border-radius: 5; -fx-font-weight: bold;");

        alert.showAlertBox("Pista aplicada", String.format("Fila %d Col %d = %d", r+1, c+1, value), "Ayuda");
    }
}


