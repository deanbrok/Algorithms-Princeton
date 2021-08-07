import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private final int[] tiles;
    private final int n;
    private int hamming;
    private int manhattan;
    private int blankIndex;


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.n = tiles.length;
        this.tiles = new int[n*n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[getIndex(i, j)] = tiles[i][j];
                if (tiles[i][j] == 0) this.blankIndex = getIndex(i, j);
            }
        }

        // Calculate the Hamming distance
        for (int i = 0; i < n*n; i++) {
            if (this.tiles[i] != 0) {
                if (i + 1 != this.tiles[i]) this.hamming++;
            }
        }

        // Calculate the Manhattan distance
        for (int i = 0; i < n*n; i++) {
            if (this.tiles[i] == 0) continue;

            int[] currentPosition = getPosition(i);
            int[] originalPosition = getOriginalPosition(this.tiles[i]);
            int vDistance = Math.abs(originalPosition[0] - currentPosition[0]);
            int hDistance = Math.abs(originalPosition[1] - currentPosition[1]);

            this.manhattan += vDistance + hDistance;
        }


    }

    /**
     * Return a string representation of the board
     * @return a string representation of the board
     */
    public String toString() {
        StringBuilder result = new StringBuilder(n + "\n");

        for (int i = 0; i < n*n; i++) {
            if (i % n == 0 && i > 0) result.append("\n");
            result.append(String.format("%2d ", tiles[i]));

        }

        return result.toString();
    }

    /**
     * Return the dimension of the board
     * @return dimension of the board
     */
    public int dimension() {
        return n;
    }

    /**
     * Return the Hamming distance
     * @return Hamming distance
     */
    public int hamming() { return hamming; }

    /**
     * Return the Manhattan distance
     * @return Manhattan distance
     */
    public int manhattan() { return manhattan; }

    /**
     * Check if the board is in the correct order
     * @return a boolean indicating whether the board is in the correct order
     */
    public boolean isGoal() {
        return hamming == 0;
    }

    @Override
    public boolean equals(Object y) {
        if (this == y) return true;
        if (!(y instanceof Board)) return false;
        Board board = (Board) y;
        return Arrays.equals(tiles, board.tiles);
    }

    /**
     * Create an Iterable of all neighbouring boards
     * @return an Iterable of all neighbouring boards
     */
    public Iterable<Board> neighbors() {
        List<Board> neighbours = new ArrayList<>();
        int[] blankPosition = getPosition(blankIndex);

        for (int i = -1; i <= 1; i += 2) {
            int row = blankPosition[0] + i;
            if (row >= 0 && row < n) {
                int[] copyTiles = tiles.clone();
                swap(blankIndex, getIndex(row, blankPosition[1]), copyTiles);
                neighbours.add(new Board(to2dArray(copyTiles)));
            }
        }

        for (int i = -1; i <= 1; i += 2) {
            int column = blankPosition[1] + i;
            if (column >= 0 && column < n) {
                int[] copyTiles = tiles.clone();
                swap(blankIndex, getIndex(blankPosition[0], column), copyTiles);
                neighbours.add(new Board(to2dArray(copyTiles)));
            }
        }

        return neighbours;
    }

    /**
     * Create a board that is a twin of the current board by exchanging two items
     * @return a Board that is a twin of the current board
     */
    public Board twin() {
        int i = 0;
        int j = 1;

        if(tiles[i] == 0) i++;
        if(tiles[j] == 0 || i == j) j++;

        int[] copyTiles = tiles.clone();
        swap(i, j, copyTiles);

        return new Board(to2dArray(copyTiles));
    }

    /**
     * Get the index of the 1d representation of the 2d array given the row and the column
     * @param row row
     * @param col column
     * @return 1d index
     */
    private int getIndex(int row, int col) {
        return row * n + col;
    }

    /**
     * Method to get the position (x-, y-coordinates) for the index
     * @param index current index
     * @return an array with the x-, y-coordinates for the index
     */
    private int[] getPosition(int index) {
        int row = index / n;
        int col = index - row * n;

        int[] result = {row, col};
        return result;
    }

    /**
     * Get the original position of the given tile
     * @param tile the given tile
     * @return the original position of the given tile
     */
    private int[] getOriginalPosition(int tile) {
        int row = (tile - 1) / n;
        int col = (tile - 1) - row * n;

        int[] result = {row, col};
        return result;
    }

    /**
     * Swap items at index i and index j in the given tiles array
     * @param i first index
     * @param j second index
     * @param tiles the array
     */
    private void swap(int i, int j, int[] tiles) {
        int temp = tiles[i];
        tiles[i] = tiles[j];
        tiles[j] = temp;
    }

    /**
     * Convert the current n*n 1d array to a n-by-n 2d array
     * @param array 1d array
     * @return a 2d array representation of the 2d array
     */
    private int[][] to2dArray(int[] array) {

        int[][] result = new int[n][n];

        for (int i = 0; i < n*n; i++) {
            int[] position = getPosition(i);
            result[position[0]][position[1]] = array[i];
        }

        return result;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
//        int[][] tiles = {{8, 1, 3},
//                         {4, 0, 2},
//                         {7, 6, 5}};
//
//        Board b = new Board(tiles);
//
//        System.out.println(b);
//
//        System.out.println("Hamming: " + b.hamming());
//        System.out.println("Manhattan: " + b.manhattan());

        int[][] tiles = {{1, 0, 3},
                         {4, 2, 5},
                         {7, 8, 6}};

        Board b = new Board(tiles);

        for (Board board: b.neighbors()) {
            System.out.println(board);
        }

        System.out.println(b);
        System.out.println(b.twin());








    }
}
