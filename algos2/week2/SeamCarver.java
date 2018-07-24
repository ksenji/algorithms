import java.awt.Color;
import java.util.Arrays;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

  private Picture picture;

  private final boolean normal;

  public SeamCarver(Picture picture) {
    this(new Picture(requireNonNull(picture, "picture cannot be null")), true);
  }

  private SeamCarver(Picture picture, boolean normal) {
    this.picture = picture;
    this.normal = normal;
  }

  public Picture picture() {
    return new Picture(picture);
  }

  public int width() {
    return normal ? picture.width() : picture.height();
  }

  public int height() {
    return normal ? picture.height() : picture.width();
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
    int deltaX = differenceSquaredSum(get(col - 1, row), get(col + 1, row));
    int deltaY = differenceSquaredSum(get(col, row - 1), get(col, row + 1));
    return Math.sqrt(deltaX + deltaY);
  }

  public int[] findHorizontalSeam() {
    return flip().findVerticalSeam();
  }

  public int[] findVerticalSeam() {
    int w = width();
    int h = height();
    int len = w * h;

    int[] edgeTo = new int[len];
    double[] pathTo = new double[len];
    Arrays.fill(pathTo, Double.POSITIVE_INFINITY);
    double[] energies = new double[len];
    for (int row = 0; row < h; row++) {
      for (int col = 0; col < w; col++) {
        energies[encode(col, row)] = energy(col, row);
      }
    }

    int p = 0;
    double min = Double.POSITIVE_INFINITY;
    for (int row = 0; row < h; row++) {
      for (int col = 0; col < w; col++) {
        int idx = encode(col, row);
        double energy = energies[idx];
        if (pathTo[idx] == Double.POSITIVE_INFINITY) {
          pathTo[idx] = energy;
        }
        double parentEnergy = pathTo[idx];
        if (row < h - 1) {
          if (col > 0) {
            updateEnergy(edgeTo, pathTo, energies, col - 1, row + 1, idx, parentEnergy);
          }
          updateEnergy(edgeTo, pathTo, energies, col, row + 1, idx, parentEnergy);
          if (col < w - 1) {
            updateEnergy(edgeTo, pathTo, energies, col + 1, row + 1, idx, parentEnergy);
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

    int[] seams = new int[h];
    int j = seams.length - 1;
    do {
      seams[j] = decode(p);
      p = edgeTo[p];
    } while (j-- > 0);

    return seams;
  }

  public void removeHorizontalSeam(int[] seam) {
    this.picture = flip().removeSeam(requireNonNull(seam, "seam cannot be null"));
  }

  public void removeVerticalSeam(int[] seam) {
    this.picture = removeSeam(requireNonNull(seam, "seam cannot be null"));
  }

  private Picture removeSeam(int[] seam) {
    int w = width();
    int h = height();

    if (seam.length != h) {
      throw new IllegalArgumentException();
    }

    int prev = -1;
    for (int s : seam) {
      if (prev != -1 && Math.abs(s - prev) > 1) {
        throw new IllegalArgumentException();
      }
      prev = s;
    }
    Picture picture = newPicture(w - 1, h);
    for (int col = 0; col < w; col++) {
      for (int row = 0; row < h; row++) {
        int x = col;
        int y = row;
        if (col > seam[row]) {
          x--;
        }
        if (col != seam[row]) {
          setRGB(picture, x, y, getRGB(col, row));
        }
      }
    }
    return picture;
  }

  private void updateEnergy(int[] edgeTo, double[] pathTo, double[] energies, int col, int row, int parent,
      double parentEnergy) {
    int k = encode(col, row);
    double newEnergy = energies[k] + parentEnergy;
    if (newEnergy < pathTo[k]) {
      pathTo[k] = newEnergy;
      edgeTo[k] = parent;
    }
  }

  private int encode(int col, int row) {
    return row * width() + col;
  }

  private int decode(int idx) {
    return idx % width();
  }

  private SeamCarver flip() {
    return new SeamCarver(picture, !normal);
  }

  private Picture newPicture(int width, int height) {
    return normal ? new Picture(width, height) : new Picture(height, width);
  }

  private Color get(int col, int row) {
    return normal ? picture.get(col, row) : picture.get(row, col);
  }

  private int getRGB(int col, int row) {
    return normal ? picture.getRGB(col, row) : picture.getRGB(row, col);
  }

  private void setRGB(Picture picture, int col, int row, int rgb) {
    if (normal) {
      picture.setRGB(col, row, rgb);
    } else {
      picture.setRGB(row, col, rgb);
    }
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

  private static <T> T requireNonNull(T t, String message) {
    if (t == null) {
      throw new IllegalArgumentException(message);
    }
    return t;
  }
}
