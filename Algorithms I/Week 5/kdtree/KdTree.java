/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private static class Node {
        private final Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p) {
            this.p = p;
        }
    }

    private Node root;
    private int size;

    public KdTree() {
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    /**
     * Check the orientation of the node we are at
     *
     * @param level the number of levels of the tree that we have traversed so far
     * @return a boolean indicating whether the current orientation is vertical or not
     */
    private static boolean isVertical(int level) {
        return level % 2 == 0;
    }

    /**
     * Check whether a point p is the tree.
     *
     * @param p the point to be found
     * @return a boolean representing whether the point is in the tree or not
     */
    public boolean contains(Point2D p) {
        return contains(p, root, 0);
    }

    private boolean contains(Point2D p, Node node, int level) {
        if (p == null) throw new IllegalArgumentException();
        if (node == null) return false;
        if (node.p.equals(p)) return true;

        if (isVertical(level)) {
            if (p.x() < node.p.x()) return contains(p, node.lb, level + 1);
            else return contains(p, node.rt, level + 1);
        }
        else {
            if (p.y() < node.p.y()) return contains(p, node.lb, level + 1);
            else return contains(p, node.rt, level + 1);
        }
    }

    public void insert(Point2D p) {
        root = insert(p, root, 0, new Point2D(0, 0), new Point2D(1, 1));
    }

    private Node insert(Point2D p, Node node, int level, Point2D min, Point2D max) {
        if (p == null) throw new IllegalArgumentException();
        if (node == null) return createNode(p, level, min, max);
        if (node.p.equals(p)) return node;

        if (isVertical(level)) {
            if (p.x() < node.p.x())
                node.lb = insert(p, node.lb, level + 1, min, new Point2D(node.p.x(), max.y()));
            else node.rt = insert(p, node.rt, level + 1, new Point2D(node.p.x(), min.y()), max);

        }
        else {
            if (p.y() < node.p.y())
                node.lb = insert(p, node.lb, level + 1, min, new Point2D(max.x(), node.p.y()));
            else node.rt = insert(p, node.rt, level + 1, new Point2D(min.x(), node.p.y()), max);
        }
        return node;
    }

    private Node createNode(Point2D p, int level, Point2D min, Point2D max) {
        Node n = new Node(p);

        // Create rect representing the separating line for drawing/debugging
        if (isVertical(level)) {
            n.rect = new RectHV(p.x(), min.y(), p.x(), max.y());
        }
        else {
            n.rect = new RectHV(min.x(), p.y(), max.x(), p.y());
        }

        this.size++;

        return n;
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        List<Point2D> list = new ArrayList<>();

        range(rect, root, 0, list, new RectHV(0, 0, 1, 1));

        return list;
    }

    private void range(RectHV queryRect, Node node, int level, List<Point2D> list,
                       RectHV boundingBox) {
        if (node == null) return;

        if (queryRect.contains(node.p)) list.add(node.p);

        if (isVertical(level)) {
            RectHV leftBox = new RectHV(boundingBox.xmin(), boundingBox.ymin(), node.p.x(),
                                        boundingBox.ymax());
            if (queryRect.intersects(leftBox))
                range(queryRect, node.lb, level + 1, list, leftBox);

            RectHV rightBox = new RectHV(node.p.x(), boundingBox.ymin(), boundingBox.xmax(),
                                         boundingBox.ymax());
            if (queryRect.intersects(rightBox))
                range(queryRect, node.rt, level + 1, list, rightBox);

        }
        else {
            RectHV lowerBox = new RectHV(boundingBox.xmin(), boundingBox.ymin(), boundingBox.xmax(),
                                         node.p.y());
            if (queryRect.intersects(lowerBox)) {
                range(queryRect, node.lb, level + 1, list, lowerBox);
            }

            RectHV upperBox = new RectHV(boundingBox.xmin(), node.p.y(), boundingBox.xmax(),
                                         boundingBox.ymax());
            if (queryRect.intersects(upperBox)) {
                range(queryRect, node.rt, level + 1, list, upperBox);
            }
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;

        return nearest(p, root.p, root, 0, new RectHV(0, 0, 1, 1));
    }

    private Point2D nearest(Point2D queryPoint, Point2D nearestPoint, Node node, int level,
                            RectHV boundingBox) {
        if (node == null) return nearestPoint;


        if (queryPoint.distanceSquaredTo(node.p) < queryPoint.distanceSquaredTo(nearestPoint))
            nearestPoint = node.p;


        if (isVertical(level)) {
            RectHV leftBox = new RectHV(boundingBox.xmin(), boundingBox.ymin(), node.p.x(),
                                        boundingBox.ymax());

            RectHV rightBox = new RectHV(node.p.x(), boundingBox.ymin(), boundingBox.xmax(),
                                         boundingBox.ymax());

            // Check which is side the query point is in
            if (queryPoint.x() < node.p.x()) {
                nearestPoint = nearest(queryPoint, nearestPoint, node.lb, level + 1, leftBox);
                if (nearestPoint.distanceSquaredTo(queryPoint) > rightBox.distanceSquaredTo(
                        queryPoint))
                    nearestPoint = nearest(queryPoint, nearestPoint, node.rt, level + 1, rightBox);
            }
            else {
                nearestPoint = nearest(queryPoint, nearestPoint, node.rt, level + 1, rightBox);
                if (nearestPoint.distanceSquaredTo(queryPoint) > leftBox.distanceSquaredTo(
                        queryPoint))
                    nearestPoint = nearest(queryPoint, nearestPoint, node.lb, level + 1, leftBox);
            }

        }
        else {
            RectHV lowerBox = new RectHV(boundingBox.xmin(), boundingBox.ymin(), boundingBox.xmax(),
                                         node.p.y());

            RectHV upperBox = new RectHV(boundingBox.xmin(), node.p.y(), boundingBox.xmax(),
                                         boundingBox.ymax());

            if (queryPoint.y() < node.p.y()) {
                nearestPoint = nearest(queryPoint, nearestPoint, node.lb, level + 1, lowerBox);
                if (nearestPoint.distanceSquaredTo(queryPoint) > upperBox.distanceSquaredTo(
                        queryPoint))
                    nearestPoint = nearest(queryPoint, nearestPoint, node.rt, level + 1, upperBox);
            }
            else {
                nearestPoint = nearest(queryPoint, nearestPoint, node.rt, level + 1, upperBox);
                if (nearestPoint.distanceSquaredTo(queryPoint) > lowerBox.distanceSquaredTo(
                        queryPoint))
                    nearestPoint = nearest(queryPoint, nearestPoint, node.lb, level + 1, lowerBox);
            }
        }

        return nearestPoint;


    }

    public void draw() {
        draw(root, 0);
    }

    private void draw(Node node, int level) {
        if (node == null) return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.02);

        node.p.draw();

        StdDraw.setPenRadius(0.005);

        if (isVertical(level)) StdDraw.setPenColor(StdDraw.RED);
        else StdDraw.setPenColor(StdDraw.BLUE);

        node.rect.draw();

        draw(node.lb, level + 1);
        draw(node.rt, level + 1);
    }

}
