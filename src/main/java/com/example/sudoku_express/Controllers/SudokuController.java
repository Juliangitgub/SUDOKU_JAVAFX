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


public class SudokuController {

    @FXML private GridPane sudokuGrid;
    @FXML private Button restartButton;
    @FXML private Button helpButton;

    private final Board board = Board.getInstance();
    private final Validator validator = new Validator();
    private final HintSolver hintSolver = new HintSolver();
    private final AlertBox alertBox = new AlertBox();

    private final TextField[][] cells = new TextField[6][6];
    private TextField selectedCell = null;
    private int selectedRow = -1;
    private int selectedCol = -1;

    @FXML
    public void initialize() {
        linkCellsFromGrid();
        loadModelToView();
        configureCellEvents();
        configureButtons();
    }

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
            }
        }
    }

    
    private void configureCellEvents() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                TextField tf = cells[r][c];
                if (tf == null) continue;

                final int row = r;
                final int col = c;

                // Click -> seleccionar celda
                tf.setOnMouseClicked(ev -> selectCell(row, col));

                // Entrada de texto
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

                    if (ch.matches("[1-6]")) {
                        int number = Integer.parseInt(ch);

                        boolean valid = true;
                        int[][] current = board.getBoard();

                        for (int i = 0; i < 6 && valid; i++) {
                            if (i != col && current[row][i] == number) valid = false; 
                            if (i != row && current[i][col] == number) valid = false; 
                        }

                        int blockRow = (row / 2) * 2;
                        int blockCol = (col / 3) * 3;
                        for (int rr = blockRow; rr < blockRow + 2 && valid; rr++) {
                            for (int cc = blockCol; cc < blockCol + 3; cc++) {
                                if (rr == row && cc == col) continue;
                                if (current[rr][cc] == number) valid = false;
                            }
                        }

                        board.cellMod(row, col, number);
                        tf.setText(String.valueOf(number));
                        tf.setStyle(valid ? styleValid() : styleInvalid());

                        Platform.runLater(this::checkWinCondition);
                        ev.consume();
                    } else {
                        ev.consume();
                    }
                });

                
                tf.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
                    if (tf.isDisabled()) {
                        ev.consume();
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

    
    private void configureButtons() {
        restartButton.setOnAction(e -> confirmAndRestart());
        helpButton.setOnAction(e -> applyHelpHint());
    }

    
    private void selectCell(int row, int col) {
        TextField tf = cells[row][col];
        if (tf == null) return;

        if (selectedCell != null && selectedCell != tf) {
            TextField prev = selectedCell;
            prev.setStyle(styleNormal());
        }

        selectedCell = tf;
        selectedRow = row;
        selectedCol = col;

        if (!tf.isDisabled()) tf.setStyle(styleSelected());
    }

    
    private void confirmAndRestart() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar reinicio");
        confirm.setHeaderText("Reiniciar tablero");
        confirm.setContentText("¿Deseas reiniciar el Sudoku?");

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            board.regenerateBoard();
            loadModelToView();
            alertBox.showAlertBox("Reinicio", "Se generó un nuevo tablero.", "Reinicio exitoso");
            selectedCell = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    
    private void applyHelpHint() {
        Hint hint = hintSolver.generateHint(board.getBoard(), board.getFixed());
        if (hint == null) {
            alertBox.showWarningAlertBox("Ayuda", "No hay pistas disponibles.", "Sin pistas");
            return;
        }

        int r = hint.row;
        int c = hint.col;
        int value = hint.value;

        TextField tf = cells[r][c];
        board.cellMod(r, c, value);
        tf.setText(String.valueOf(value));
        tf.setStyle(styleHint());
        alertBox.showAlertBox("Pista", String.format("Sugerencia: fila %d columna %d = %d", r + 1, c + 1, value), "Ayuda");

        Platform.runLater(this::checkWinCondition);
    }

    
    private void checkWinCondition() {
        int[][] grid = board.getBoard();


        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (grid[r][c] == 0) {
                    return; 
                }
            }
        }

        for (int r = 0; r < 6; r++) {
            boolean[] fila = new boolean[7];
            boolean[] columna = new boolean[7];

            for (int c = 0; c < 6; c++) {
                int numFila = grid[r][c];
                int numCol = grid[c][r];

                if (fila[numFila]) return; 
                if (columna[numCol]) return;

                fila[numFila] = true;
                columna[numCol] = true;
            }
        }

        for (int blockRow = 0; blockRow < 6; blockRow += 2) {
            for (int blockCol = 0; blockCol < 6; blockCol += 3) {
                boolean[] blockCheck = new boolean[7];
                for (int r = blockRow; r < blockRow + 2; r++) {
                    for (int c = blockCol; c < blockCol + 3; c++) {
                        int num = grid[r][c];
                        if (blockCheck[num]) return;
                        blockCheck[num] = true;
                    }
                }
            }
        }

        Platform.runLater(() -> {
            alertBox.showAlertBox("🎉 ¡Felicidades!", "Has completado correctamente el Sudoku.", "Victoria");
        });
    }


    /* ---------------------- Estilos ---------------------- */

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



