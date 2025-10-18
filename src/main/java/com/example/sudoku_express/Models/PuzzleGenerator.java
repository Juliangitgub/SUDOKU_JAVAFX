package com.example.sudoku_express.Models;

import java.util.*;

/**
 * Generador de tableros Sudoku 6x6 (bloques 2x3).
 * Crea una solución completa válida y luego un tablero visible
 * con solo 2 valores fijos por bloque.
 */
public class PuzzleGenerator {

    private final int SIZE = 6;
    private final int BLOCK_ROWS = 2;
    private final int BLOCK_COLS = 3;

    private int[][] puzzle;
    private boolean[][] boolPuzzle;
    private int[][] solution;

    private final Random random = new Random();

    /** Constructor: genera tablero inicial */
    public PuzzleGenerator() {
        generateNewPuzzle();
    }

    /** Reinicia el tablero completamente */
    public void resetPuzzle() {
        generateNewPuzzle();
    }

    /** Genera una nueva solución y tablero visible */
    private void generateNewPuzzle() {
        // 🔹 Paso 1: generar solución completa
        solution = generateFullSolution(SIZE, SIZE, BLOCK_ROWS, BLOCK_COLS);

        // 🔹 Paso 2: generar tablero visible desde la solución
        puzzle = generatePuzzleFromSolution(solution, SIZE, SIZE, BLOCK_ROWS, BLOCK_COLS);

        // 🔹 Paso 3: marcar celdas fijas
        boolPuzzle = new boolean[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boolPuzzle[r][c] = puzzle[r][c] != 0;
            }
        }
    }

    // ==========================================================
    //  MÉTODOS PRINCIPALES (usados por Board y Controller)
    // ==========================================================

    public int[][] getPuzzle() {
        return puzzle;
    }

    public boolean[][] getBoolPuzzle() {
        return boolPuzzle;
    }

    public int[][] getSolution() {
        return solution;
    }

    // ==========================================================
    //  GENERADOR DE SOLUCIÓN COMPLETA (BACKTRACKING)
    // ==========================================================

    /**
     * Genera una cuadrícula Sudoku 6x6 completa y válida.
     */
    public int[][] generateFullSolution(int rows, int cols, int blockRows, int blockCols) {
        int[][] grid = new int[rows][cols];
        solveSudoku(grid, rows, cols, blockRows, blockCols);
        return grid;
    }

    /**
     * Resuelve el Sudoku por backtracking (rellenando toda la cuadrícula).
     */
    private boolean solveSudoku(int[][] grid, int rows, int cols, int blockRows, int blockCols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 0) {
                    List<Integer> nums = new ArrayList<>();
                    for (int n = 1; n <= cols; n++) nums.add(n);
                    Collections.shuffle(nums, random);

                    for (int num : nums) {
                        if (isValid(grid, r, c, num, rows, cols, blockRows, blockCols)) {
                            grid[r][c] = num;
                            if (solveSudoku(grid, rows, cols, blockRows, blockCols))
                                return true;
                            grid[r][c] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verifica si el número puede colocarse en (row, col) respetando las reglas de Sudoku 6x6.
     */
    private boolean isValid(int[][] grid, int row, int col, int num, int rows, int cols, int blockRows, int blockCols) {
        for (int i = 0; i < cols; i++) {
            if (grid[row][i] == num || grid[i][col] == num)
                return false;
        }

        int startRow = (row / blockRows) * blockRows;
        int startCol = (col / blockCols) * blockCols;

        for (int r = 0; r < blockRows; r++) {
            for (int c = 0; c < blockCols; c++) {
                if (grid[startRow + r][startCol + c] == num)
                    return false;
            }
        }
        return true;
    }

    // ==========================================================
    //  GENERADOR DEL TABLERO VISIBLE (2 CELDAS FIJAS POR BLOQUE)
    // ==========================================================

    /**
     * Crea un tablero visible a partir de la solución completa.
     * Deja solo 2 celdas visibles por bloque 2x3.
     */
    public int[][] generatePuzzleFromSolution(int[][] solution, int rows, int cols, int blockRows, int blockCols) {
        int[][] puzzle = new int[rows][cols];

        // Copiar la solución completa
        for (int r = 0; r < rows; r++) {
            System.arraycopy(solution[r], 0, puzzle[r], 0, cols);
        }

        // Para cada bloque 2x3, dejar visibles solo 2 celdas aleatorias
        for (int br = 0; br < rows; br += blockRows) {
            for (int bc = 0; bc < cols; bc += blockCols) {
                List<int[]> cells = new ArrayList<>();

                for (int r = 0; r < blockRows; r++) {
                    for (int c = 0; c < blockCols; c++) {
                        cells.add(new int[]{br + r, bc + c});
                    }
                }

                Collections.shuffle(cells, random);
                for (int i = 2; i < cells.size(); i++) { // deja solo 2 visibles
                    int[] cell = cells.get(i);
                    puzzle[cell[0]][cell[1]] = 0;
                }
            }
        }

        return puzzle;
    }

    /**
     * Genera directamente un nuevo tablero Sudoku válido (solución incluida).
     * Mantiene compatibilidad con versiones antiguas.
     */
    public int[][] generatePuzzle(int rows, int cols, int blockRows, int blockCols) {
        int[][] full = generateFullSolution(rows, cols, blockRows, blockCols);
        return generatePuzzleFromSolution(full, rows, cols, blockRows, blockCols);
    }
}

