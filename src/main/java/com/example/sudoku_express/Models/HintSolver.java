package com.example.sudoku_express.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HintSolver genera pistas basadas en la solución real del tablero.
 * Compatible con el métoo generateHint(board, fixed) usado en SudokuController.
 */
public class HintSolver {

    private final Random random = new Random();

    /**
     * Genera una pista válida para una celda vacía.
     * Usa la solución completa almacenada en Board.
     *
     * @param current tablero visible (con ceros)
     * @param fixed matriz de celdas fijas
     * @return una Hint (row, col, value) o null si no hay celdas vacías
     */
    public Hint generateHint(int[][] current, boolean[][] fixed) {
        Board board = Board.getInstance();
        int[][] solution = board.getSolution();

        if (solution == null) return null;

        List<Hint> available = new ArrayList<>();

        // Buscar todas las celdas vacías editables
        for (int r = 0; r < current.length; r++) {
            for (int c = 0; c < current[r].length; c++) {
                if (!fixed[r][c] && current[r][c] == 0) {
                    available.add(new Hint(r, c, solution[r][c]));
                }
            }
        }

        if (available.size()<=1) return null;

        // Seleccionar una celda aleatoria
        return available.get(random.nextInt(available.size()));
    }
}
