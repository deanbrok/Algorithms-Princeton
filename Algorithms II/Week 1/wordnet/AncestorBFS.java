/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;

public class AncestorBFS {

    private final HashMap<Integer, Integer> distA;
    private final HashMap<Integer, Integer> distB;

    private int shortestAncestor;
    private int shortestDistance;


    public AncestorBFS(Digraph G, Iterable<Integer> A, Iterable<Integer> B) {
        this.distA = new HashMap<>();
        this.distB = new HashMap<>();
        shortestAncestor = -1;
        shortestDistance = Integer.MAX_VALUE;

        Queue<Integer> qA = new Queue<>();
        Queue<Integer> qB = new Queue<>();

        for (Integer i : A) {
            if (i == null) throw new IllegalArgumentException();
            qA.enqueue(i);
            distA.put(i, 0);
        }

        for (Integer i : B) {
            if (i == null) throw new IllegalArgumentException();
            qB.enqueue(i);
            distB.put(i, 0);

            if (distA.containsKey(i)) {
                int ancestralDistance = 0;
                shortestAncestor = i;
                shortestDistance = ancestralDistance;
            }
        }

        lockStepBFS(G, qA, qB);

    }

    private void lockStepBFS(Digraph G, Queue<Integer> qA, Queue<Integer> qB) {
        boolean turnA = true;
        boolean loopBreakA = false;
        boolean loopBreakB = false;

        while ((!qA.isEmpty() || !qB.isEmpty()) && (!loopBreakA || !loopBreakB)) {

            if (turnA) {
                turnA = false;
                if (qA.isEmpty()) continue;

                int v = qA.dequeue();

                for (int w : G.adj(v)) {
                    if (!distA.containsKey(w)) {
                        int distance = distA.get(v) + 1;
                        if (distance > shortestDistance) {
                            loopBreakA = true;
                            break;
                        }

                        if (distB.containsKey(w)) {
                            int ancestralDistance = distance + distB.get(w);

                            if (ancestralDistance < shortestDistance) {
                                shortestAncestor = w;
                                shortestDistance = ancestralDistance;
                            }
                        }

                        distA.put(w, distance);
                        qA.enqueue(w);
                    }

                }

            }
            else {
                turnA = true;
                if (qB.isEmpty()) continue;

                int v = qB.dequeue();

                for (int w : G.adj(v)) {
                    if (!distB.containsKey(w)) {
                        int distance = distB.get(v) + 1;
                        if (distance > shortestDistance) {
                            loopBreakB = true;
                            break;
                        }

                        if (distA.containsKey(w)) {
                            int ancestralDistance = distance + distA.get(w);

                            if (ancestralDistance < shortestDistance) {
                                shortestAncestor = w;
                                shortestDistance = ancestralDistance;
                            }
                        }

                        distB.put(w, distance);
                        qB.enqueue(w);
                    }
                }
            }
        }
    }


    public int shortestAncestor() {
        return shortestAncestor;
    }

    public int shortestDistance() {
        if (shortestAncestor == -1) return -1;
        else return shortestDistance;
    }

    public static void main(String[] args) {

    }
}
