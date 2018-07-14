import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
  private final int n;

  private final int top;

  private final int bottom;

  private final boolean[][] grid;

  private final WeightedQuickUnionUF uf;

  private final WeightedQuickUnionUF bwp; // backwash prevention

  private int openSites;

  public Percolation(int n) {
    if (n <= 0) {
      throw new IllegalArgumentException();
    }
    this.n = n;
    top = 0;
    bottom = n * n + 1;
    grid = new boolean[n][n];
    uf = new WeightedQuickUnionUF(bottom + 1);
    bwp = new WeightedQuickUnionUF(bottom);
  }

  public void open(int row, int col) {
    if (!isOpen(row, col)) {
      grid[row - 1][col - 1] = true;
      openSites++;

      int idx = idx(row, col);
      if (row > 1) {
        if (isOpen(row - 1, col)) {
          uf.union(idx(row - 1, col), idx);
          bwp.union(idx(row - 1, col), idx);
        }
      } else {
        uf.union(top, idx);
        bwp.union(top, idx);
      }

      if (col > 1) {
        if (isOpen(row, col - 1)) {
          uf.union(idx(row, col - 1), idx);
          bwp.union(idx(row, col - 1), idx);
        }
      }

      if (col < n) {
        if (isOpen(row, col + 1)) {
          uf.union(idx(row, col + 1), idx);
          bwp.union(idx(row, col + 1), idx);
        }
      }

      if (row < n) {
        if (isOpen(row + 1, col)) {
          uf.union(idx(row + 1, col), idx);
          bwp.union(idx(row + 1, col), idx);
        }
      } else {
        uf.union(bottom, idx);
      }
    }
  }

  public boolean isOpen(int row, int col) {
    validate(row, col);
    return grid[row - 1][col - 1];
  }

  public boolean isFull(int row, int col) {
    return isOpen(row, col) && bwp.connected(top, idx(row, col));
  }

  public int numberOfOpenSites() {
    return openSites;
  }

  public boolean percolates() {
    return uf.connected(top, bottom);
  }

  private void validate(int row, int col) {
    if (row < 1 || col < 1 || row > n || col > n) {
      throw new IllegalArgumentException();
    }
  }

  private int idx(int row, int col) {
    return (row - 1) * n + col;
  }
}
