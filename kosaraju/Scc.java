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
      int[] standings = new int[V];
      Stack<Integer> finish = new Stack<>();
      Stack<Integer> s = new Stack<>();
      int c = 0;
      for (int i = 0; i < V; i++) {
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
            standings[c++] = finish.pop();
          }
        }
      }
      visited = new boolean[V];
      for (int i = V - 1; i >= 0; i--) {
        int v = standings[i];
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
    System.out.println(sb.toString());

    /* We get different results depending on which node is explored next in the DFS. In my iterative version, I explored in the reverse order essentially as we push them on to the stack. If you explore them in the same order, we get different results: 434821,968,459,313,278,211,205,197,177,162,152*/

    /* Outputs: 434821,968,459,313,211,205,197,177,173,168,162 
     * If we use the alogrithm from Coursera class from the Stanford professor who does the exploration first on the reverse graph and then pick the SCC on the original graph by exploring nodes based on their relative standings*/

    /* Outputs:  434821,971,460,412,395,313,312,304,211,206,197 if we 
     * use https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm or the one in https://web.stanford.edu/class/cs97si/06-basic-graph-algorithms.pdf
     * Both of those algorithms talk about exporing DFS (maintaining their relative order of explorations on the original Graph) and then to pick the SCC run the algorithm on the reverse graph
     */
  }
}
