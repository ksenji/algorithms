import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import edu.princeton.cs.algs4.Digraph;

public class SAP {
  private final Digraph g;

  // constructor takes a digraph (not necessarily a DAG)
  public SAP(Digraph g) {
    this.g = new Digraph(requireNonNull(g, "g cannot be null"));
  }

  // length of shortest ancestral path between v and w; -1 if no such path
  public int length(int v, int w) {
    return length(Arrays.asList(v), Arrays.asList(w));
  }

  // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
  public int ancestor(int v, int w) {
    return ancestor(Arrays.asList(v), Arrays.asList(w));
  }

  // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
  public int length(Iterable<Integer> v, Iterable<Integer> w) {
    Node n = ancestorInternal(v, w);
    return (n != null) ? n.len : -1;
  }

  // a common ancestor that participates in shortest ancestral path; -1 if no such path
  public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
    Node n = ancestorInternal(v, w);
    return (n != null) ? n.id : -1;
  }

  private Node ancestorInternal(Iterable<Integer> v, Iterable<Integer> w) {
    List<Node> vNodes = bfs(requireNonNull(v, "v cannot be null"));
    List<Node> wNodes = bfs(requireNonNull(w, "w cannot be null"));

    Map<Integer, Integer> wNodesMap = new HashMap<>();
    for (Node wNode : wNodes) {
      wNodesMap.put(wNode.id, wNode.len);
    }

    Node min = null;
    for (Node vNode : vNodes) {
      int id = vNode.id;
      if (wNodesMap.containsKey(id)) {
        int len = vNode.len + wNodesMap.get(id);
        if (min == null) {
          min = new Node(id, len);
        } else if (min.len > len) {
          min = new Node(id, len);
        }
      }
    }
    return min;
  }

  private List<Node> bfs(Iterable<Integer> v) {
    Queue<Node> q = new LinkedList<>();
    List<Node> nodes = new ArrayList<>();
    v.forEach(id -> {
      if (id == null) {
        throw new IllegalArgumentException();
      }
      q.add(new Node(id, 0));
    });
    //    if (q.isEmpty()) {
    //      throw new IllegalArgumentException();
    //    }
    BitSet visited = new BitSet(g.V());
    while (!q.isEmpty()) {
      Node head = q.poll();
      if (head.id < 0 || head.id >= g.V()) {
        throw new IllegalArgumentException();
      }
      if (!visited.get(head.id)) {
        visited.set(head.id);
        nodes.add(head);
        g.adj(head.id).forEach(id -> q.add(new Node(id, head.len + 1)));
      }
    }
    return nodes;
  }

  private static final class Node {
    final int id;

    final int len;

    Node(int id, int len) {
      this.id = id;
      this.len = len;
    }
  }

  private static <T> T requireNonNull(T t, String message) {
    if (t == null) {
      throw new IllegalArgumentException(message);
    }
    return t;
  }
}
