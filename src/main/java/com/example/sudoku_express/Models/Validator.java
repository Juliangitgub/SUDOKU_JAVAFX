package com.example.sudoku_express.Models;

public class Validator {

    public boolean isValidPlacement(int row, int col, int num, int[][] board,int SUB_ROWS,int SUB_COLS) {
        // Validar fila y columna
        for (int i = 0; i < 6; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        // Validar bloque 2x3
        int startRow = (row / SUB_ROWS) * SUB_ROWS;
        int startCol = (col / SUB_COLS) * SUB_COLS;
        for (int r = startRow; r < startRow + SUB_ROWS; r++) {
            for (int c = startCol; c < startCol + SUB_COLS; c++) {
                if (board[r][c] == num) {
                    return false;
                }
            }
        }
        return true;
    }
}
