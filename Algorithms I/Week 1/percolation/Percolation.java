/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final WeightedQuickUnionUF uf;
    private final WeightedQuickUnionUF backwashUf;
    private final int n;
    private boolean[] sites;
    private int openedSites = 0;



    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        uf = new WeightedQuickUnionUF(n*n + 2);
        backwashUf = new WeightedQuickUnionUF(n*n + 1);
        this.n = n;
        sites = new boolean[n*n + 2];

        connectVirtualSites();
    }

    private void connectVirtualSites() {
        for (int i = 1; i <= n; i++) {
            uf.union(0, i);
            uf.union(n * n + 1, getIndex(n, i));
            backwashUf.union(0, i);
        }
    }

    private int getIndex(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) throw new IllegalArgumentException();
        return row * n - n + col;
    }

    private int[] getAdjacent(int row, int col) {
        int up = row - 1;
        int down = row + 1;
        int left = col - 1;
        int right = col + 1;

        int[] adjacents = new int[4];

        if (up > 0) adjacents[0] = getIndex(up, col);
        if (down <= n) adjacents[1] = getIndex(down, col);
        if (left > 0) adjacents[2] = getIndex(row, left);
        if (right <= n) adjacents[3] = getIndex(row, right);

        return adjacents;
    }



    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) return;

        int index = getIndex(row, col);
        sites[index] = true;
        openedSites += 1;

        int[] adjacents = getAdjacent(row, col);

        for (int i: adjacents) {
            if (sites[i]) {
                uf.union(index, i);
                backwashUf.union(index, i);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        return sites[getIndex(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        int index = getIndex(row, col);

        return sites[index] && backwashUf.find(0) == backwashUf.find(index);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openedSites;
    }

    // does the system percolate?
    public boolean percolates() {
        if (n == 1) return isOpen(1, 1);

        return (uf.find(0) == uf.find(n*n + 1));
    }

}
