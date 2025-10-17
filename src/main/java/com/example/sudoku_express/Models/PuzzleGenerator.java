package com.example.sudoku_express.Models;

import java.util.Random;

/**
 * Esta clase se encargara de generar el tablero para nuestro sudoku
 * */
public class PuzzleGenerator {
    public Validator validator =new Validator();
    private int [][] board;
    private boolean[][] fixed;
    private final Random random = new Random();
    private static final int SIZE = 6;        // Tamaño del tablero
    private static final int SUB_ROWS = 2;    // Filas por bloque
    private static final int SUB_COLS = 3;    // Columnas por bloque
    //Constructor por defecto sin argumentos

    public PuzzleGenerator(){
        cleanBoard();
        Generate();
    }
    public void cleanBoard(){
        this.board=new int[SIZE][SIZE];
        this.fixed = new boolean[SIZE][SIZE];
        //establecemos los valores de inicio en ceros
        for (int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                board[fila][col] = 0;      // 0 significa "celda vacía"
                fixed[fila][col] = false;  // false = editable
            }
        }
    }
    public int[][] getPuzzle(){return this.board;}
    public boolean[][] getBoolPuzzle(){return this.fixed;}


    public void Generate(){
        // Recorre los 6 bloques (0 a 5)
        for (int block = 0; block < SIZE; block++) {
            int startRow = (block / 3) * 2;
            int startCol = (block % 3) * 3;

            int placed = 0;
            int attempts = 0;

            // Rellenar 2 números válidos por bloque
            while (placed < 2 && attempts < 30) {
                int row = startRow + random.nextInt(SUB_ROWS);
                int col = startCol + random.nextInt(SUB_COLS);
                if (board[row][col] != 0) {
                    attempts++;
                    continue; // ya ocupada
                }

                int num = 1 + random.nextInt(SIZE);
                if (validator.isValidPlacement(row, col, num,board,SUB_ROWS,SUB_COLS)) {
                    board[row][col] = num;
                    fixed[row][col] = true;
                    placed++;
                }
                attempts++;
            }

            // Si no se logró llenar bien el bloque, reiniciar
            if (placed < 2) {
                cleanBoard();
                block = -1; // volver al primer bloque
            }
        }
    }
    public void resetPuzzle() {
        Generate(); // Reutiliza la lógica de generación
    }
}
