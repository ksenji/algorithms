import java.awt.Color;
import java.util.Arrays;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

  private Picture picture;

  public SeamCarver(Picture picture) {
    this.picture = new Picture(requireNonNull(picture, "picture cannot be null"));
  }

  public Picture picture() {
    return new Picture(this.picture);
  }

  public int width() {
    return this.picture.width();
  }

  public int height() {
    return this.picture.height();
  }

  public double energy(int col, int row) {
    int w = width() - 1;
    int h = height() - 1;
    if (col < 0 || col > w || row < 0 || row > h) {
      throw new IllegalArgumentException();
    }
    if (row == 0 || col == 0 || col == w || row == h) {
      return 1000d;
    }
    int deltaX = differenceSquaredSum(picture.get(col - 1, row), picture.get(col + 1, row));
    int deltaY = differenceSquaredSum(picture.get(col, row - 1), picture.get(col, row + 1));
    return Math.sqrt(deltaX + deltaY);
  }

  public int[] findHorizontalSeam() {
    return findSeam(Orientation.HORIZONTAL);
  }

  public int[] findVerticalSeam() {
    return findSeam(Orientation.VERTICAL);
  }

  public void removeHorizontalSeam(int[] seam) {
    removeSeam(Orientation.HORIZONTAL, requireNonNull(seam, "seam cannot be null"));
  }

  public void removeVerticalSeam(int[] seam) {
    removeSeam(Orientation.VERTICAL, requireNonNull(seam, "seam cannot be null"));
  }

  private void removeSeam(Orientation orientation, int[] seam) {
    boolean vertical = (orientation == Orientation.VERTICAL);
    int w = width();
    int h = height();

    if (vertical) {
      if (vertical && seam.length != h || seam.length != w) {
        throw new IllegalArgumentException();
      }
    } else if (seam.length != w) {
      throw new IllegalArgumentException();
    }
    int prev = -1;
    for (int s : seam) {
      if (prev != -1 && Math.abs(s - prev) > 1) {
        throw new IllegalArgumentException();
      }
      prev = s;
    }
    Picture pict = new Picture(vertical ? w - 1 : w, vertical ? h : h - 1);
    for (int col = 0; col < w; col++) {
      for (int row = 0; row < h; row++) {
        int x = col;
        int y = row;
        if (vertical) {
          int idx = seam[row];
          if (prev == -1) {
            prev = idx;
          }
          if (col > seam[row]) {
            x--;
          } else if (col == seam[row]) {
            continue;
          }
        } else {
          if (row > seam[col]) {
            y--;
          } else if (row == seam[col]) {
            continue;
          }
        }
        pict.setRGB(x, y, picture.getRGB(col, row));
      }
    }
    this.picture = pict;
  }

  private int[] findSeam(Orientation orientation) {
    int w = width();
    int h = height();
    int len = w * h;

    int[] edgeTo = new int[len];
    double[] pathTo = new double[len];
    Arrays.fill(pathTo, Double.POSITIVE_INFINITY);
    double[] energies = new double[len];
    for (int row = 0; row < h; row++) {
      for (int col = 0; col < w; col++) {
        energies[encode(col, row, w)] = energy(col, row);
      }
    }

    boolean vertical = (orientation == Orientation.VERTICAL);
    int p = 0;
    double min = Double.POSITIVE_INFINITY;
    if (vertical) {
      for (int row = 0; row < h; row++) {
        for (int col = 0; col < w; col++) {
          int idx = encode(col, row, w);
          double energy = energies[idx];
          if (pathTo[idx] == Double.POSITIVE_INFINITY) {
            pathTo[idx] = energy;
          }
          double parentEnergy = pathTo[idx];
          if (row < h - 1) {
            if (col > 0) {
              updateEnergy(w, edgeTo, pathTo, energies, col - 1, row + 1, idx, parentEnergy);
            }
            updateEnergy(w, edgeTo, pathTo, energies, col, row + 1, idx, parentEnergy);
            if (col < w - 1) {
              updateEnergy(w, edgeTo, pathTo, energies, col + 1, row + 1, idx, parentEnergy);
            }
          }
          if (row == h - 1) {
            if (min > parentEnergy) {
              min = parentEnergy;
              p = idx;
            }
          }
        }
      }
    } else {
      for (int col = 0; col < w; col++) {
        for (int row = 0; row < h; row++) {
          int idx = encode(col, row, w);
          double energy = energies[idx];
          if (pathTo[idx] == Double.POSITIVE_INFINITY) {
            pathTo[idx] = energy;
          }
          double parentEnergy = pathTo[idx];
          if (col < w - 1) {
            if (row > 0) {
              updateEnergy(w, edgeTo, pathTo, energies, col + 1, row - 1, idx, parentEnergy);
            }
            updateEnergy(w, edgeTo, pathTo, energies, col + 1, row, idx, parentEnergy);
            if (row < h - 1) {
              updateEnergy(w, edgeTo, pathTo, energies, col + 1, row + 1, idx, parentEnergy);
            }
          }
          if (col == w - 1) {
            if (min > parentEnergy) {
              min = parentEnergy;
              p = idx;
            }
          }
        }
      }
    }

    int[] seams = new int[vertical ? h : w];
    int j = seams.length - 1;
    do {
      seams[j] = vertical ? decodeCol(p, w) : decodeRow(p, w);
      p = edgeTo[p];
    } while (j-- > 0);

    return seams;
  }

  private void updateEnergy(int width, int[] edgeTo, double[] pathTo, double[] energies, int col, int row, int parent,
      double parentEnergy) {
    int k = encode(col, row, width);
    double newEnergy = energies[k] + parentEnergy;
    if (newEnergy < pathTo[k]) {
      pathTo[k] = newEnergy;
      edgeTo[k] = parent;
    }
  }

  private int encode(int col, int row, int w) {
    return row * w + col;
  }

  private int decodeCol(int idx, int w) {
    return idx % w;
  }

  private int decodeRow(int idx, int w) {
    return idx / w;
  }

  private int differenceSquaredSum(Color c1, Color c2) {
    // @formatter:off
    return square(c1.getRed() - c2.getRed()) + 
           square(c1.getBlue() - c2.getBlue()) + 
           square(c1.getGreen() - c2.getGreen());
    // @formatter:on
  }

  private int square(int num) {
    return num * num;
  }

  private enum Orientation {
    HORIZONTAL, VERTICAL
  }

  private static <T> T requireNonNull(T t, String message) {
    if (t == null) {
      throw new IllegalArgumentException(message);
    }
    return t;
  }
}
