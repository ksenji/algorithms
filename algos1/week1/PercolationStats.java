import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

  private final double mean;

  private final double stddev;

  private final double offset;

  public PercolationStats(int n, int trials) {
    if (n <= 0 || trials <= 0) {
      throw new IllegalArgumentException();
    }
    double[] threshold = new double[trials];
    double nsquare = n * n * 1d;
    for (int i = 0; i < trials; i++) {
      Percolation p = new Percolation(n);
      while (!p.percolates()) {
        int row = StdRandom.uniform(1, n + 1);
        int col = StdRandom.uniform(1, n + 1);
        p.open(row, col);
      }
      threshold[i] = p.numberOfOpenSites() / nsquare;
    }
    mean = StdStats.mean(threshold);
    stddev = StdStats.stddev(threshold);
    offset = 1.96 * stddev / Math.sqrt(trials);
  }

  public double mean() {
    return mean;
  }

  public double stddev() {
    return stddev;
  }

  public double confidenceLo() {
    return mean - offset;
  }

  public double confidenceHi() {
    return mean + offset;
  }

  public static void main(String[] args) {
    PercolationStats stats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    System.out.printf("%s%22s%s%n", "mean", " = ", stats.mean());
    System.out.printf("%s%20s%s%n", "stddev", " = ", stats.stddev());
    System.out.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");
  }
}
