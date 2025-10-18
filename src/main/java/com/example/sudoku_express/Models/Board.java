package com.example.sudoku_express.Models;

/**
 * Clase Board (tablero principal del Sudoku 6x6).
 * Gestiona el tablero visible, las celdas fijas y su solución completa.
 */
public class Board {

    private static Board instance; // 🔹 instancia única

    private final PuzzleGenerator puzzleGenerator = new PuzzleGenerator();

    private int[][] board;       // tablero visible (con ceros para celdas ocultas)
    private boolean[][] fixed;   // celdas fijas (visibles desde el inicio)
    private int[][] solution;    // solución completa (solo el modelo la conoce)

    /** Constructor privado: genera el tablero inicial. */
    private Board() {
        generateNewBoard();
    }

    /** Singleton: obtiene la instancia única. */
    public static Board getInstance() {
        if (instance == null) instance = new Board();
        return instance;
    }

    /** Genera un nuevo tablero válido y guarda la solución completa. */
    private void generateNewBoard() {
        int rows = 6, cols = 6, blockRows = 2, blockCols = 3;

        // 🔹 Paso 1: Generar una solución válida completa
        solution = puzzleGenerator.generateFullSolution(rows, cols, blockRows, blockCols);

        // 🔹 Paso 2: Generar el tablero visible (ocultando celdas)
        board = puzzleGenerator.generatePuzzleFromSolution(solution, rows, cols, blockRows, blockCols);

        // 🔹 Paso 3: Marcar las celdas fijas (no editables)
        fixed = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                fixed[r][c] = board[r][c] != 0;
            }
        }
    }

    /** Devuelve si una celda es fija (no editable). */
    public boolean cellSGet(int x, int y) {
        return fixed[x][y];
    }

    /** Devuelve el valor visible de una celda (0 si está vacía). */
    public int cellVGet(int x, int y) {
        return board[x][y];
    }

    /** Cambia el valor visible de una celda editable. */
    public void cellMod(int x, int y, int value) {
        if (!fixed[x][y]) {
            board[x][y] = value;
        }
    }

    /** Regenera el tablero y la solución. */
    public void regenerateBoard() {
        generateNewBoard();
    }

    /** Devuelve el tablero actual (lo que ve el usuario). */
    public int[][] getBoard() {
        return board;
    }

    /** Devuelve las celdas fijas. */
    public boolean[][] getFixed() {
        return fixed;
    }

    /** Devuelve la solución completa del Sudoku. */
    public int[][] getSolution() {
        return solution;
    }

    /** Comprueba si el valor ingresado por el usuario es correcto. */
    public boolean isCorrectValue(int row, int col, int value) {
        return solution != null && solution[row][col] == value;
    }
}
