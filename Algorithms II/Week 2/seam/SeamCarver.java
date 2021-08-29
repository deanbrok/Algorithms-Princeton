/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    private Picture p;
    private double[][] energy;
    private boolean isTransposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("The picture cannot be null.");
        p = new Picture(picture);

        energy = new double[height()][width()];
        for (int y = 0; y < height(); y++)
            for (int x = 0; x < width(); x++)
                energy[y][x] = -1;
    }

    // current picture
    public Picture picture() {
        if (isTransposed) transposePicture();
        return new Picture(p);
    }

    // width of current picture
    public int width() {
        return p.width();
    }

    // height of current picture
    public int height() {
        return p.height();
    }

    private double gradient(int x1, int y1, int x2, int y2) {
        double gradient = 0;
        for (int i = 0; i <= 2; i++) {
            gradient += centralDifferenceSquared(x1, y1, x2, y2, i);
        }
        return gradient;
    }

    private double centralDifferenceSquared(int x1, int y1, int x2, int y2, int color) {
        assert color <= BLUE;

        Color pixel1 = p.get(x1, y1);
        Color pixel2 = p.get(x2, y2);

        if (color == RED)           return Math.pow(pixel2.getRed() - pixel1.getRed(), 2);
        else if (color == GREEN)    return Math.pow(pixel2.getGreen() - pixel1.getGreen(), 2);
        else                        return Math.pow(pixel2.getBlue() - pixel1.getBlue(), 2);
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) throw new IllegalArgumentException("x and y must be in the range");
        if (x - 1 < 0 || x + 1 > width() - 1 || y - 1 < 0 || y + 1 > height() - 1) return 1000;

        return Math.sqrt(gradient(x - 1, y, x + 1, y) + gradient(x, y - 1, x, y + 1));
    }

    private void initializeArrays(double[][] distTo) {
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (y == 0) distTo[y][x] = 0;
                else        distTo[y][x] = Double.POSITIVE_INFINITY;
            }
        }
    }

    private void relax(int x, int y, double[][] distTo, int[][] edgeTo) {
        if (y + 1 > height() - 1) return;
        int currentY = y + 1;

        for (int i = -1; i <= 1; i++) {
            int currentX = x + i;
            if (currentX < 0 || currentX > width() - 1) continue;
            if (energy[currentY][currentX] < 0) energy[currentY][currentX] = energy(currentX, currentY);

            if (distTo[currentY][currentX] > distTo[y][x] + energy[currentY][currentX]) {
                distTo[currentY][currentX] = distTo[y][x] + energy[currentY][currentX];
                edgeTo[currentY][currentX] = x;
            }
        }
    }

    private int[] findSeam(boolean isHorizontal) {
        if ((isHorizontal && !isTransposed) || (!isHorizontal && isTransposed)) transposePicture();

        double[][] distTo = new double[height()][width()];
        // double[][] energy = new double[height()][width()];
        int[][] edgeTo = new int[height()][width()];

        initializeArrays(distTo);

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                relax(x, y, distTo, edgeTo);
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minVertex = 0;

        for (int x = 0; x < width(); x++) {
            if (distTo[height() - 1][x] < minDist) {
                minVertex = x;
                minDist = distTo[height() - 1][x];
            }
        }

        int[] result = new int[height()];
        result[height() - 1] = minVertex;

        int currentVertex = minVertex;
        for (int y = height() - 1; y > 0; y--) {
            currentVertex = edgeTo[y][currentVertex];
            result[y - 1] = currentVertex;
        }

        if (isTransposed) transposePicture();
        return result;
    }

    private void transposePicture() {
        if (isTransposed) isTransposed = false;
        else              isTransposed = true;

        Picture newP = new Picture(height(), width());
        double[][] newEnergy = new double[width()][height()];

        for (int y = 0; y < newP.height(); y++) {
            for (int x = 0; x < newP.width(); x++) {
                newP.setRGB(x, y, p.getRGB(y, x));
                newEnergy[y][x] = energy[x][y];
            }

        }

        energy = newEnergy;
        p = newP;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeam(false);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findSeam(true);
    }

    private void validateSeam(int[] seam, int s) {
        if (seam[s] < 0 || seam[s] > width() - 1) throw new IllegalArgumentException("Seam element is out of range.");
        for (int i = -1; i <= 1; i++) {
            int adj = s + i;
            if (adj < 0 || adj > height() - 1)     continue;
            if (Math.abs(seam[adj] - seam[s]) > 1) throw new IllegalArgumentException("Adjacent elements differ by more than one.");
        }
    }

    private void removeSeam(int[] seam, boolean isHorizontal) {
        if ((isHorizontal && !isTransposed) || (!isHorizontal && isTransposed)) transposePicture();

        if (seam == null) throw new IllegalArgumentException("The seam cannot be null");
        if (width() <= 1) throw new IllegalArgumentException("The picture has a width of 1, cannot remove seam.");
        if (seam.length != height()) throw new IllegalArgumentException("Invalid seam length");

        Picture newP = new Picture(width() - 1, height());

        for (int y = 0; y < height(); y++) {
            validateSeam(seam, y);
            int newX = 0;
            for (int x = 0; x < width(); x++) {
                if (x != seam[y]) {
                    energy[y][newX] = energy[y][x];
                    newP.setRGB(newX++, y, p.getRGB(x, y));
                }
            }
        }

        p = newP;

        for (int y = 0; y < height(); y++) {
            int x = seam[y];
            if (x <= width() - 1) energy[y][x] = energy(x, y);
            if (x - 1 >= 0) energy[y][x - 1] = energy(x - 1, y);
        }

        if (isTransposed) transposePicture();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
       removeSeam(seam, false);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        removeSeam(seam, true);
    }
}

