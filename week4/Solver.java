package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

  private Iteration sol;

  public Solver(Board initial) {
    if (initial == null) {
      throw new IllegalArgumentException();
    }

    MinPQ<Iteration> main = new MinPQ<>();
    main.insert(new Iteration(initial, copyAndAdd(Collections.emptyList(), initial)));

    Board twinBoard = initial.twin();
    MinPQ<Iteration> twin = new MinPQ<>();
    twin.insert(new Iteration(twinBoard, copyAndAdd(Collections.emptyList(), twinBoard)));

    MinPQ<Iteration> pq = main;
    while (!pq.isEmpty()) {
      Iteration top = pq.delMin();
      if (top.board.isGoal()) {
        if (pq == main) {
          sol = top;
        }
        break;
      }

      Board prev = null;
      if (top.path.size() > 1) {
        prev = top.path.get(top.path.size() - 2);
      }
      for (Board neighbor : top.board.neighbors()) {
        if (!neighbor.equals(prev)) {
          pq.insert(top.next(neighbor));
        }
      }

      if (pq == main) {
        if (!twin.isEmpty()) {
          pq = twin;
        }
      } else if (!main.isEmpty()) {
        pq = main;
      }
    }
  }

  public boolean isSolvable() {
    return sol != null;
  }

  public int moves() {
    return sol != null ? sol.path.size() - 1 : -1;
  }

  public Iterable<Board> solution() {
    return sol != null ? sol.path::iterator : null;
  }

  private static List<Board> copyAndAdd(List<Board> paths, Board board) {
    List<Board> copy = new ArrayList<>(paths);
    copy.add(board);
    return copy;
  }

  public static void main(String[] args) {
    // create initial board from file
    In in = new In("/Users/ksenji/work/coursera/week4/8puzzle/puzzle04.txt");
    int n = in.readInt();
    int[][] blocks = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        blocks[i][j] = in.readInt();
    Board initial = new Board(blocks);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable())
      StdOut.println("No solution possible");
    else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
        StdOut.println(board);
    }
  }

  private static final class Iteration implements Comparable<Iteration> {
    private final Board board;

    private final List<Board> path;

    private final int priority;

    Iteration(Board board, List<Board> path) {
      this.board = board;
      this.path = path;
      this.priority = board.manhattan() + path.size();
    }

    @Override
    public int compareTo(Iteration o) {
      return Integer.compare(priority(), o.priority());
    }

    private int priority() {
      return priority;
    }

    Iteration next(Board next) {
      return new Iteration(next, copyAndAdd(path, next));
    }
  }
}
