package com.example.sudoku_express.Models;

/**
 * Representa una pista sugerida por el solver.
 */
public class Hint {
    public final int row;
    public final int col;
    public final int value;

    public Hint(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}

