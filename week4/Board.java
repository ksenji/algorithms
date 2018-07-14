import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

  private final int[][] blocks;

  private int hamming;

  private int manhattan;

  private int r;

  private int c;

  private int tw1i = -1;

  private int tw1j = -1;

  private int tw2i = -1;

  private int tw2j = -1;

  public Board(int[][] blocks) {
    if (blocks == null) {
      throw new IllegalArgumentException();
    }
    this.blocks = copyOfBlocks(blocks);
    int n = blocks.length;
    hamming = 0;
    manhattan = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        int val = blocks[i][j];
        if (val != 0) {
          if (tw1i == -1) {
            tw1i = i;
            tw1j = j;
          } else if (tw2i == -1) {
            tw2i = i;
            tw2j = j;
          }
          if (val != (i * n) + j + 1) {
            hamming++;
            int row = val / n;
            int col = val % n;
            if (col == 0) {
              row--;
              col = n;
            }
            manhattan += Math.abs(row - i) + Math.abs(col - j - 1);
          }
        } else {
          r = i;
          c = j;
        }
      }
    }
  }

  public int dimension() {
    return blocks.length;
  }

  public int hamming() {
    return this.hamming;
  }

  public int manhattan() {
    return this.manhattan;
  }

  public boolean isGoal() {
    return this.hamming == 0;
  }

  public Board twin() {
    int[][] copy = copyOfBlocks(blocks);
    swap(copy, tw1i, tw1j, tw2i, tw2j);
    return new Board(copy);
  }

  private void swap(int[][] blocks, int x, int y, int i, int j) {
    int tmp = blocks[x][y];
    blocks[x][y] = blocks[i][j];
    blocks[i][j] = tmp;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Board other = (Board) obj;
    if (!Arrays.deepEquals(blocks, other.blocks))
      return false;
    return true;
  }

  public Iterable<Board> neighbors() {
    List<Board> neighbors = new ArrayList<>(4);
    if (c > 0) {
      int[][] copy = copyOfBlocks(blocks);
      copy[r][c] = copy[r][c - 1];
      copy[r][c - 1] = 0;
      neighbors.add(new Board(copy));
    }
    if (c < blocks.length - 1) {
      int[][] copy = copyOfBlocks(blocks);
      copy[r][c] = copy[r][c + 1];
      copy[r][c + 1] = 0;
      neighbors.add(new Board(copy));
    }
    if (r > 0) {
      int[][] copy = copyOfBlocks(blocks);
      copy[r][c] = copy[r - 1][c];
      copy[r - 1][c] = 0;
      neighbors.add(new Board(copy));
    }
    if (r < blocks.length - 1) {
      int[][] copy = copyOfBlocks(blocks);
      copy[r][c] = copy[r + 1][c];
      copy[r + 1][c] = 0;
      neighbors.add(new Board(copy));
    }
    return neighbors::iterator;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(blocks.length);
    sb.append("\n");
    for (int i = 0; i < blocks.length; i++) {
      int[] block = blocks[i];
      sb.append(" ");
      for (int j = 0; j < block.length; j++) {
        sb.append(block[j]);
        if (j < block.length - 1) {
          sb.append("  ");
        }
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  private int[][] copyOfBlocks(int[][] blocks) {
    int[][] copy = new int[blocks.length][blocks.length];
    for (int i = 0; i < blocks.length; i++) {
      copy[i] = Arrays.copyOf(blocks[i], blocks[i].length);
    }
    return copy;
  }
}
