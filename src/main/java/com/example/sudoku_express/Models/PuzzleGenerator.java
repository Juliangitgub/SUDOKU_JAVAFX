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


    public void Generate() {
        for (int blockRow = 0; blockRow < SIZE / SUB_ROWS; blockRow++) {
            for (int blockCol = 0; blockCol < SIZE / SUB_COLS; blockCol++) {

                int startRow = blockRow * SUB_ROWS;
                int startCol = blockCol * SUB_COLS;

                int placed = 0;
                int attempts = 0;

                while (placed < 2 && attempts < 30) {
                    int row = startRow + random.nextInt(SUB_ROWS);
                    int col = startCol + random.nextInt(SUB_COLS);

                    if (board[row][col] != 0) {
                        attempts++;
                        continue;
                    }

                    int num = 1 + random.nextInt(SIZE);
                    if (validator.isValidPlacement(row, col, num,board,2,3)) {
                        board[row][col] = num;
                        fixed[row][col] = true;
                        placed++;
                    }
                    attempts++;
                }

                if (placed < 2) {
                    cleanBoard();
                    blockRow = -1; // reinicia ambos bucles
                    break;
                }
            }
        }
    }

    public void resetPuzzle() {
        Generate(); // Reutiliza la lógica de generación
    }
}
