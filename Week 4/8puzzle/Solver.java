import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Comparator;

public class Solver {

    private final int moves;
    private final boolean solvable;
    private Node lastNode;


    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();


        MinPQ<Node> pqOriginal = new MinPQ<>(new ManhattanPriority());
        pqOriginal.insert(new Node(initial, 0, null));

        MinPQ<Node> pqTwin = new MinPQ<>(new ManhattanPriority());
        pqTwin.insert(new Node(initial.twin(), 0, null));

        Node minOriginal;
        Node minTwin;

        int count = 0;
        while (!pqOriginal.min().board.isGoal() && !pqTwin.min().board.isGoal()) {

            if (count % 2 == 0) {
                minOriginal = pqOriginal.delMin();
                for (Board n: minOriginal.board.neighbors()) {
                    if (minOriginal.previous == null || !n.equals(minOriginal.previous.board))
                        pqOriginal.insert(new Node(n, minOriginal.moves + 1, minOriginal));
                }
            } else {
                minTwin = pqTwin.delMin();
                for (Board n: minTwin.board.neighbors()) {
                    if (minTwin.previous == null || !n.equals(minTwin.previous.board))
                        pqTwin.insert(new Node(n, minTwin.moves + 1, minTwin));
                }
            }
            count++;
        }

        if(pqOriginal.min().board.isGoal()) {
            lastNode = pqOriginal.delMin();
            moves = lastNode.moves;
            solvable = true;
        } else {
            lastNode = null;
            moves = -1;
            solvable = false;
        }

    }

    private class Node {
        private final Board board;
        private final int moves;
        private final Node previous;
        private final int hamming;
        private final int manhattan;


        public Node(Board board, int moves, Node previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            this.hamming = board.hamming();
            this.manhattan = board.manhattan();
        }
    }

    private class HammingPriority implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            int p1 = o1.hamming + o1.moves;
            int p2 = o2.hamming + o2.moves;

            if (p1 < p2) return -1;
            else if (p1 > p2) return 1;
            else return 0;
        }

    }

    private class ManhattanPriority implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            int p1 = o1.manhattan + o1.moves;
            int p2 = o2.manhattan + o2.moves;

            if (p1 < p2) return -1;
            else if (p1 > p2) return 1;
            else return 0;
        }

    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        ArrayList<Board> solution = new ArrayList<>();
        Node currentNode = lastNode;

        if (solvable) {
            while (currentNode != null) {
                solution.add(0, currentNode.board);
                currentNode = currentNode.previous;
            }
            return solution;
        }
        return null;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}