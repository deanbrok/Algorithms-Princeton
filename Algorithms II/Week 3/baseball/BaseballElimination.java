/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {

    private final int N;
    private final int pairs;

    private int maxWin;
    private int maxWinTeam;

    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] matchMatrix;
    private final String[] indexToName;
    private final HashMap<String, Integer> nameToIndex;

    private final boolean[] isEliminated;
    private final Set<String>[] certificates;


    public BaseballElimination(String filename) {
        In in = new In(filename);

        N = Integer.parseInt(in.readLine());
        maxWin = Integer.MIN_VALUE;
        pairs = (int) choose(N - 1, 2);

        matchMatrix = new int[N][N];
        wins = new int[N];
        losses = new int[N];
        remaining = new int[N];
        indexToName = new String[N];
        nameToIndex = new HashMap<>();

        isEliminated = new boolean[N];
        certificates =  (HashSet<String>[]) new HashSet[N];

        int t = 0;
        while (in.hasNextLine()) {
            String[] line = in.readLine().replaceFirst("^[\\s]+", "").split("[\\s]+");
            addTeam(line, t++);
        }

        for (int i = 0; i < N; i++) {
            checkElimination(i);
        }
    }

    private void addTeam(String[] line, int i) {
        indexToName[i] = line[0];
        nameToIndex.put(line[0], i);
        wins[i] = Integer.parseInt(line[1]);
        losses[i] = Integer.parseInt(line[2]);
        remaining[i] = Integer.parseInt(line[3]);

        if (maxWin < wins[i]) {
            maxWin = wins[i];
            maxWinTeam = i;
        }

        for (int j = 4; j < 4 + N; j++) {
            matchMatrix[i][j - 4] = Integer.parseInt(line[j]);
        }
    }

    private void checkElimination(int team) {
        int possibleWins = wins[team] + remaining[team];

        if (possibleWins < maxWin) {
            isEliminated[team] = true;
            certificates[team] = new HashSet<>(Collections.singleton(indexToName[maxWinTeam]));
        } else {
            FlowNetwork network = createNetwork(team, possibleWins);

            int s = network.V() - 2;
            int t = network.V() - 1;

            FordFulkerson f = new FordFulkerson(network, s, t);

            for (FlowEdge e: network.adj(s)) {
                if (e. capacity() > 0 && e.flow() < e.capacity()) {
                    isEliminated[team] = true;
                    createCertificate(team, f);
                    break;
                }
            }

            // if (team == 2) System.out.println(network);

        }
    }

    private FlowNetwork createNetwork(int team, int possibleWins) {
        FlowNetwork network = new FlowNetwork(pairs + N - 1 + 2);

        int s = network.V() - 2;
        int t = network.V() - 1;

        // Connect team vertices to target
        for (int i = 0; i < N - 1; i++) {
            network.addEdge(new FlowEdge(i, t, possibleWins - wins[getActualTeam(i, team)] ));
        }

        int networkIndex = N - 1;

        for (int i = 0; i < N; i++) {
            if (i == team) continue;
            for (int j = i + 1; j < N; j++) {
                if (j != team) {

                    // if (matchMatrix[i][j] > 0) {
                        // Connect start to game vertices
                    network.addEdge(new FlowEdge(s, networkIndex, matchMatrix[i][j]));

                        // Connect game vertices to team vertices
                    network.addEdge(new FlowEdge(networkIndex, getNetworkTeam(i, team), Double.POSITIVE_INFINITY));
                    network.addEdge(new FlowEdge(networkIndex, getNetworkTeam(j, team), Double.POSITIVE_INFINITY));
                    // }

                    networkIndex++;
                }
            }
        }

        return network;
    }

    private int getActualTeam(int i, int team) {
        int actualTeam = i;
        if (i >= team) ++actualTeam;
        return actualTeam;
    }

    private int getNetworkTeam(int i, int team) {
        int networkTeam = i;
        if (i >= team) --networkTeam;
        return networkTeam;
    }
    private void createCertificate(int team, FordFulkerson f) {
        for (int v = 0; v < N - 1; v++) {
            if (f.inCut(v)) {
                if (certificates[team] == null) certificates[team] = new HashSet<>(Collections.singleton(indexToName[getActualTeam(v, team)]));
                else                            certificates[team].add(indexToName[getActualTeam(v, team)]);
            }
        }
    }

    private static double choose(int x, int y) {
        if (y < 0 || y > x) return 0;
        if (y > x/2) {
            // choose(n,k) == choose(n,n-k),
            // so this could save a little effort
            y = x - y;
        }

        double denominator = 1.0, numerator = 1.0;
        for (int i = 1; i <= y; i++) {
            denominator *= i;
            numerator *= (x + 1 - i);
        }
        return numerator / denominator;
    }

    public int numberOfTeams() { return N; }

    public Iterable<String> teams() { return Arrays.asList(indexToName); }

    public int wins(String team) {
        if (!nameToIndex.containsKey(team)) throw new IllegalArgumentException();
        return wins[nameToIndex.get(team)];
    }

    public int losses(String team) {
        if (!nameToIndex.containsKey(team)) throw new IllegalArgumentException();
        return losses[nameToIndex.get(team)];
    }

    public int remaining(String team) {
        if (!nameToIndex.containsKey(team)) throw new IllegalArgumentException();
        return remaining[nameToIndex.get(team)];
    }

    public int against(String team1, String team2) {
        if (!nameToIndex.containsKey(team1) || !nameToIndex.containsKey(team2)) throw new IllegalArgumentException();
        return matchMatrix[nameToIndex.get(team1)][nameToIndex.get(team2)];
    }

    public boolean isEliminated(String team) {
        if (!nameToIndex.containsKey(team)) throw new IllegalArgumentException();
        return isEliminated[nameToIndex.get(team)];
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (!nameToIndex.containsKey(team)) throw new IllegalArgumentException();
        return certificates[nameToIndex.get(team)];
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("teams54.txt");

        // BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
