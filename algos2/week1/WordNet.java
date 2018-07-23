import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

public class WordNet {

  private final Map<Integer, String> synsetMap;

  private final Map<String, List<Integer>> nounsMap;

  private final SAP sap;

  // constructor takes the name of the two input files
  public WordNet(String synsets, String hypernyms) {
    if (synsets == null || hypernyms == null) {
      throw new IllegalArgumentException();
    }
    nounsMap = new HashMap<>();
    synsetMap = new HashMap<>();
    In syn = new In(synsets);
    int v = 0;
    while (syn.hasNextLine()) {
      String line = syn.readLine();
      String[] words = line.split(",");
      int id = Integer.parseInt(words[0]);
      String synset = words[1];
      synsetMap.put(id, synset);
      String[] nounsArr = synset.split(" ");
      for (String noun : nounsArr) {
        List<Integer> ids = nounsMap.getOrDefault(noun, new ArrayList<>());
        ids.add(id);
        nounsMap.put(noun, ids);
      }
      v++;
    }
    Digraph g = new Digraph(v);
    In hyper = new In(hypernyms);
    while (hyper.hasNextLine()) {
      String line = hyper.readLine();
      String[] words = line.split(",");
      int id = Integer.parseInt(words[0]);
      for (int i = 1; i < words.length; i++) {
        g.addEdge(id, Integer.parseInt(words[i]));
      }
    }
    if (new DirectedCycle(g).hasCycle() || roots(g).size() > 1) {
      throw new IllegalArgumentException();
    }
    sap = new SAP(g);
  }

  // returns all WordNet nouns
  public Iterable<String> nouns() {
    return nounsMap.keySet()::iterator;
  }

  // is the word a WordNet noun?
  public boolean isNoun(String word) {
    if (word == null) {
      throw new IllegalArgumentException();
    }
    return nounsMap.containsKey(word);
  }

  // distance between nounA and nounB (defined below)
  public int distance(String nounA, String nounB) {
    validateNoun(nounA);
    validateNoun(nounB);

    return sap.length(nounsMap.get(nounA), nounsMap.get(nounB));
  }

  // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
  // in a shortest ancestral path (defined below)
  public String sap(String nounA, String nounB) {
    validateNoun(nounA);
    validateNoun(nounB);

    return synsetMap.get(sap.ancestor(nounsMap.get(nounA), nounsMap.get(nounB)));
  }

  private Set<Integer> roots(Digraph g) {
    Set<Integer> roots = new HashSet<>();
    boolean[] visited = new boolean[g.V()];
    for (int i = 0; i < visited.length; i++) {
      if (!visited[i]) {
        Stack<Integer> s = new Stack<>();
        s.push(i);
        visited[i] = true;
        while (!s.isEmpty()) {
          int top = s.pop();
          Iterable<Integer> adj = g.adj(top);
          boolean atleastOne = false;
          for (int id : adj) {
            atleastOne = true;
            if (!visited[id]) {
              visited[id] = true;
              s.push(id);
            }
          }
          if (!atleastOne) {
            roots.add(top);
          }
        }
      }
    }
    return roots;
  }

  private void validateNoun(String noun) {
    if (!isNoun(noun)) {
      throw new IllegalArgumentException();
    }
  }
}
