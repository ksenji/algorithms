import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class Scc {

  private static final class Graph {

    private final LinkedList<Integer>[] normal;

    private final LinkedList<Integer>[] reverse;

    Graph(int V) {
      this.normal = (LinkedList<Integer>[]) new LinkedList[V];
      this.reverse = (LinkedList<Integer>[]) new LinkedList[V];
    }

    public void addEdge(int v, int w) {
      addEdge(normal, v, w);
      addEdge(reverse, w, v);
    }

    private void addEdge(LinkedList<Integer>[] adj, int v, int w) {
      if (adj[v] == null) {
        adj[v] = new LinkedList<>();
      }
      adj[v].add(w);
    }
  }

  private final PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());

  public Scc(int V, String file) throws IOException {
    Graph g = new Graph(V);

    try (Scanner scanner = new Scanner(Paths.get(file), StandardCharsets.UTF_8.name())) {
      while (scanner.hasNext()) {
        int v = scanner.nextInt();
        int w = scanner.nextInt();
        g.addEdge(v - 1, w - 1); //0-index
      }

      boolean[] visited = new boolean[V];
      int[] finishTimes = new int[V];
      Stack<Integer> finish = new Stack<>();
      Stack<Integer> s = new Stack<>();
      int finishTime = 0;
      for (int i = V - 1; i >= 0; i--) {
        if (!visited[i]) {
          s.push(i);
          while (!s.isEmpty()) {
            int top = s.pop();
            if (!visited[top]) {
              visited[top] = true;
              LinkedList<Integer> edges = g.reverse[top];
              if (edges != null) {
                for (int edge : edges) {
                  s.push(edge);
                }
              }
              finish.push(top);
            }
          }
          while (!finish.isEmpty()) {
            finishTimes[finishTime++] = finish.pop();
          }
        }
      }

      visited = new boolean[V];
      for (int i = V - 1; i >= 0; i--) {
        int v = finishTimes[i];
        if (!visited[v]) {
          s.push(v);
          int count = 0;
          while (!s.isEmpty()) {
            int top = s.pop();
            if (!visited[top]) {
              visited[top] = true;
              count++;
              LinkedList<Integer> edges = g.normal[top];
              if (edges != null) {
                for (int edge : edges) {
                  s.push(edge);
                }
              }
            }
          }
          pq.offer(count);
        }
      }
    }
  }

  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {

      @Override
      public boolean hasNext() {
        return !pq.isEmpty();
      }

      @Override
      public Integer next() {
        return pq.poll();
      }
    };
  }

  public static void main(String[] args) throws IOException {
    Scc scc = new Scc(875714, args[0]);
    Iterator<Integer> iter = scc.iterator();
    int k = 0;
    while (iter.hasNext() && k++ <= 5) {
      System.out.println(iter.next());
    }
  }
}
