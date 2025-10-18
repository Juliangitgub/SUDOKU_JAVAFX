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
    private final Board board = new Board();
    private final AlertBox alert = new AlertBox();
    private final HintSolver hintSolver = new HintSolver();

    private TextField[][] cells = new TextField[6][6];

    /**
     * Se ejecuta automáticamente al cargar la vista (FXML).
     */
    @FXML
    public void initialize() {
        loadCellsFromGrid();
        fillBoardFromModel();

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

                final int r = row;
                final int c = col;
                cell.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!cell.isDisabled()) {
                        if (newVal.matches("[1-6]")) {
                            board.cellMod(r, c, Integer.parseInt(newVal));

                            // 🔍 Validación automática (fila y columna)
                            if (hasConflict(r, c)) {
                                // marca celda en rojo
                                cell.setStyle("-fx-background-color: rgba(255,0,0,0.3); -fx-border-color: red;");
                            } else {
                                // estilo normal si no hay error
                                cell.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-border-color: #d2a679;");
                            }

                        } else if (newVal.isEmpty()) {
                            board.cellMod(r, c, 0);
                            cell.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-border-color: #d2a679;");
                        } else {
                            // evita caracteres inválidos
                            cell.setText("");
                        }
                    }
                });
            }
        }
    }

    /**
     * Verifica si hay un número repetido en la misma fila o columna.
     */
    private boolean hasConflict(int row, int col) {
        int value = board.cellVGet(row, col);
        if (value == 0) return false;

        // Revisa fila
        for (int j = 0; j < 6; j++) {
            if (j != col && board.cellVGet(row, j) == value) return true;
        }

        // Revisa columna
        for (int i = 0; i < 6; i++) {
            if (i != row && board.cellVGet(i, col) == value) return true;
        }

        return false;
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
     * Usa HintSolver para obtener una pista y aplicarla.
     */
    private void onHelpClicked() {
        Hint hint = hintSolver.generateHint(board.getBoard(), board.getFixed());

        if (hint == null) {
            alert.showWarningAlertBox("Pista", "No se pudo generar una pista en este tablero.", "Sin pistas disponibles");
            return;
        }

        int r = hint.row;
        int c = hint.col;
        int value = hint.value;

        if (r < 0 || r >= 6 || c < 0 || c >= 6) {
            alert.showWarningAlertBox("Pista inválida", "Se generó una pista fuera de rango.", "Error de solver");
            return;
        }

        TextField target = cells[r][c];
        if (target == null) {
            alert.showWarningAlertBox("Pista", "La celda de la pista no se encuentra en la vista.", "Error");
            return;
        }

        board.cellMod(r, c, value);
        target.setText(String.valueOf(value));

        target.setStyle("-fx-background-color: rgba(0,255,0,0.15); "
                + "-fx-text-fill: white; -fx-border-color: #00ff00; "
                + "-fx-border-radius: 5; -fx-font-weight: bold;");

        alert.showAlertBox("Pista aplicada", String.format("Fila %d Col %d = %d", r + 1, c + 1, value), "Ayuda");
    }
}

