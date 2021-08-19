/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WordNet {

    private final HashMap<String, Set<Integer>> idLookup;
    private final HashMap<Integer, String> synsetLookup;
    private final Digraph G;

    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        idLookup = new HashMap<>();
        synsetLookup = new HashMap<>();

        // Parse synset input
        In synIn = new In(synsets);
        while (synIn.hasNextLine()) {
            String[] line = synIn.readLine().split(",");

            int id = Integer.parseInt(line[0]);
            String[] synset = line[1].split(" ");

            synsetLookup.put(id, line[1]);

            for (String s: synset) {
                if (!idLookup.containsKey(s)) idLookup.put(s, new HashSet<>(Arrays.asList(id)));
                else idLookup.get(s).add(id);
            }
        }

        // Parse hypernym input
        G = new Digraph(synsetLookup.size());
        In hypIn = new In(hypernyms);
        while (hypIn.hasNextLine()) {
            String[] line = hypIn.readLine().split(",");

            int id = Integer.parseInt(line[0]);

            for (int i = 1; i < line.length; i++) {
                G.addEdge(id, Integer.parseInt(line[i]));
            }
        }

        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle() || multipleRoots()) throw new IllegalArgumentException();

        sap = new SAP(G);
    }

    private boolean multipleRoots() {
        int count = 0;

        for (int i = 0; i < G.V(); i++) {

            if (G.outdegree(i) == 0) {
                count++;
            }

            if (count >= 2) {
                return true;
            }
        }

        if (count >= 2) return true;

        return false;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return idLookup.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        if (idLookup.containsKey(word)) return true;
        return false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        return sap.length(idLookup.get(nounA), idLookup.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        int shortestAncestor = sap.ancestor(idLookup.get(nounA), idLookup.get(nounB));
        return synsetLookup.get(shortestAncestor);
    }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet("synsets100-subgraph.txt", "hypernyms100-subgraph.txt");
    }

}
