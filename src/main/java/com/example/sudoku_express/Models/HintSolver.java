package com.example.sudoku_express.Models;

/**
 * Solver de pista única: sugiere una sola celda válida
 * sin resolver completamente el tablero.
 */
public class HintSolver {

    private static final int SIZE = 6;
    private final Validator validator = new Validator();

    /**
     * Genera una pista sugerida (sin modificar el tablero).
     *
     * @param board tablero actual (6x6)
     * @param fixed matriz booleana con las celdas fijas
     * @return Hint con fila, columna y valor sugerido, o null si no hay pista
     */
    public Hint generateHint(int[][] board, boolean[][] fixed) {
        int vacias = contarVacias(board);

        // Evita resolver el tablero si solo queda una celda vacía
        if (vacias <= 1) {
            return null;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // Solo actuar sobre celdas vacías y no fijas
                if (board[row][col] == 0 && !fixed[row][col]) {

                    for (int num = 1; num <= SIZE; num++) {
                        if (validator.isValidPlacement(row, col,num,board,2,3)) {
                            // Retornar la pista sin modificar el tablero
                            return new Hint(row, col, num);
                        }
                    }
                }
            }
        }

        return null; // No se encontró pista válida
    }

    /**
     * Cuenta cuántas celdas vacías hay en el tablero.
     */
    private int contarVacias(int[][] board) {
        int contador = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    contador++;
                }
            }
        }
        return contador;
    }
}
