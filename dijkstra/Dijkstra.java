import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Dijkstra {

  private final Graph g;

  public Dijkstra(int V, String file) throws IOException {
    g = new Graph(V);

    try (Scanner scanner = new Scanner(Paths.get(file), StandardCharsets.UTF_8.name())) {
      Pattern delimiter = scanner.delimiter();
      while (scanner.hasNext()) {
        int v = scanner.nextInt();
        scanner.useDelimiter("\\R");
        try (Scanner adjScanner = new Scanner(scanner.next())) {
          adjScanner.useDelimiter("[, \t]");
          while (adjScanner.hasNext()) {
            g.addEdge(v - 1, adjScanner.nextInt() - 1, adjScanner.nextInt());
          }
        }
        scanner.useDelimiter(delimiter);
      }
    }
  }

  public int shortestDistance(int v, int w) {
    PriorityQueue<Iteration> q = new PriorityQueue<>();
    q.offer(new Iteration(v - 1, 0));
    BitSet visited = new BitSet();
    while (!q.isEmpty()) {
      Iteration head = q.poll();
      if (head.vertex == w - 1) {
        return head.distance;
      }
      if (!visited.get(head.vertex)) {
        visited.set(head.vertex);
        g.edges(head.vertex).forEachRemaining(e -> q.offer(new Iteration(e.vertex, head.distance + e.distance)));
      }
    }
    return 1000000;
  }

  private static final class Iteration implements Comparable<Iteration> {
    final int vertex;

    final int distance;

    Iteration(int vertex, int distance) {
      this.vertex = vertex;
      this.distance = distance;
    }

    @Override
    public int compareTo(Iteration o) {
      return Integer.compare(distance, o.distance);
    }
  }

  private static final class Edge {
    final int vertex;

    final int distance;

    Edge(int vertex, int distance) {
      this.vertex = vertex;
      this.distance = distance;
    }
  }

  private static final class Graph {

    private final LinkedList<Edge>[] adj;

    Graph(int V) {
      this.adj = (LinkedList<Edge>[]) new LinkedList[V];
    }

    public void addEdge(int v, int w, int distance) {
      if (adj[v] == null) {
        adj[v] = new LinkedList<>();
      }
      adj[v].add(new Edge(w, distance));
    }

    public Iterator<Edge> edges(int v) {
      List<Edge> edges = adj[v];
      if (edges == null) {
        edges = Collections.emptyList();
      }
      return edges.iterator();
    }
  }

  public static void main(String[] args) throws Exception {
    Dijkstra d = new Dijkstra(Integer.parseInt(args[0]), args[1]);
    int[] vertices = new int[] { 7, 37, 59, 82, 99, 115, 133, 165, 188, 197 };
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < vertices.length; i++) {
      int w = vertices[i];
      sb.append(d.shortestDistance(1, w));
      if (i < vertices.length - 1) {
        sb.append(",");
      }
    }
    System.out.println(sb.toString()); //Prints: 2599,2610,2947,2052,2367,2399,2029,2442,2505,3068
  }
}
