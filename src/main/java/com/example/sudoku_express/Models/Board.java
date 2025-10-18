package com.example.sudoku_express.Models;

public class Board {
    private static Board instance;  // 🔹 instancia única

    private PuzzleGenerator puzzleGenerator = new PuzzleGenerator();
    private int[][] board = puzzleGenerator.getPuzzle();
    private boolean[][] fixed = puzzleGenerator.getBoolPuzzle();

    public Board() {}  // 🔒 evita instanciación externa

    public static Board getInstance() {
        if (instance == null) instance = new Board();
        return instance;
    }

    public boolean cellSGet(int x, int y) {
        return fixed[x][y];
    }

    public int cellVGet(int x, int y) {
        return board[x][y];
    }

    public void regenerateBoard() {
        puzzleGenerator.resetPuzzle();
        board = puzzleGenerator.getPuzzle();
        fixed = puzzleGenerator.getBoolPuzzle();
    }

    public void cellMod(int x, int y, int value) {
        board[x][y] = value;
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean[][] getFixed() {
        return fixed;
    }
}

