import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class Scc {
  private PriorityQueue<Integer> pq;

  public Scc(int V, String file) throws IOException {
    Graph g = new Graph(V);

    try (Scanner scanner = new Scanner(Paths.get(file), StandardCharsets.UTF_8.name())) {
      while (scanner.hasNext()) {
        int v = scanner.nextInt();
        int w = scanner.nextInt();
        g.addEdge(v - 1, w - 1); //0-index
      }

      int[] standings = new int[V];
      for (int i = 0; i < standings.length; i++) {
        standings[i] = i;
      }
      dfs(true, V, standings, g);
      pq = dfs(false, V, standings, g.reverse());
    }
  }

  private PriorityQueue<Integer> dfs(boolean firstPass, int V, int[] standings, Graph graph) {
    PriorityQueue<Integer> pq = firstPass ? null : new PriorityQueue<>(Collections.reverseOrder());
    boolean[] calculated = firstPass ? new boolean[V] : null;
    int[] newStandings = firstPass ? new int[V] : null;

    boolean[] visited = new boolean[V];
    Stack<Integer> s = new Stack<>();
    int t = 0;
    for (int i = V - 1; i >= 0; i--) {
      int v = standings[i];
      if (!visited[v]) {
        s.push(v);
        int count = 0;
        while (!s.isEmpty()) {
          int top = s.peek();
          if (!visited[top]) {
            visited[top] = true;
            if (!firstPass) {
              count++;
            }
            graph.edges(top).forEachRemaining(s::add);
          } else {
            if (firstPass && !calculated[top]) {
              newStandings[t++] = top;
              calculated[top] = true;
            }
            s.pop();
          }
        }
        if (!firstPass) {
          pq.offer(count);
        }
      }
    }
    if (firstPass) {
      System.arraycopy(newStandings, 0, standings, 0, standings.length);
    }
    return pq;
  }

  public Iterator<Integer> iterator() {
    PriorityQueue<Integer> copy = new PriorityQueue<>(Collections.reverseOrder());
    if (pq != null) {
      pq.forEach(copy::add);
    }
    return new Iterator<Integer>() {

      @Override
      public boolean hasNext() {
        return !copy.isEmpty();
      }

      @Override
      public Integer next() {
        return copy.poll();
      }
    };
  }

  private static final class Graph {

    private final boolean normal;

    private final LinkedList<Integer>[] forward;

    private final LinkedList<Integer>[] reverse;

    Graph(int V) {
      this.normal = true;
      this.forward = (LinkedList<Integer>[]) new LinkedList[V];
      this.reverse = (LinkedList<Integer>[]) new LinkedList[V];
    }

    private Graph(boolean normal, Graph g) {
      this.normal = normal;
      this.forward = g.forward;
      this.reverse = g.reverse;
    }

    public void addEdge(int v, int w) {
      addEdge(forward, v, w);
      addEdge(reverse, w, v);
    }

    public Iterator<Integer> edges(int v) {
      List<Integer> edges = normal ? forward[v] : reverse[v];
      if (edges == null) {
        edges = Collections.emptyList();
      }
      return edges.iterator();
    }

    public Graph reverse() {
      return new Graph(!normal, this);
    }

    private void addEdge(LinkedList<Integer>[] adj, int v, int w) {
      if (adj[v] == null) {
        adj[v] = new LinkedList<>();
      }
      adj[v].add(w);
    }
  }

  public static void main(String[] args) throws IOException {
    Scc scc = new Scc(Integer.parseInt(args[0])/* Number of Vertices */,
        args[1] /* File name containing directed edges in "u v" format without quotes */);
    Iterator<Integer> iter = scc.iterator();
    int k = 0;
    StringBuilder sb = new StringBuilder();
    while (iter.hasNext() && k++ <= 10) {
      sb.append(iter.next());
      sb.append(",");
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    System.out.println(sb.toString()); // Outputs: 434821,968,459,313,211,205,197,177,162,152,149 for the Coursera assignment
  }
}
