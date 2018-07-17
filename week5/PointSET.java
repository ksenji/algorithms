import java.util.Iterator;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
  private final TreeSet<Point2D> ts;

  public PointSET() {
    ts = new TreeSet<>();
  }

  public boolean isEmpty() {
    return ts.isEmpty();
  }

  public int size() {
    return ts.size();
  }

  public void insert(Point2D p) {
    ts.add(requireNonNull(p, "p cannot be null"));
  }

  public boolean contains(Point2D p) {
    return ts.contains(requireNonNull(p, "p cannot be null"));
  }

  public void draw() {
    ts.iterator().forEachRemaining(Point2D::draw);
  }

  public Iterable<Point2D> range(RectHV rect) {
    return rangeInternal(requireNonNull(rect, "rect cannot be null"));
  }

  public Point2D nearest(Point2D p) {
    return nearestInternal(requireNonNull(p, "p cannot be null"));
  }

  private Iterable<Point2D> rangeInternal(RectHV rect) {
    return ts.stream().filter(rect::contains)::iterator;
  }

  private Point2D nearestInternal(Point2D query) {
    Iterator<Point2D> iter = ts.iterator();
    Point2D near = null;
    double min = Double.POSITIVE_INFINITY;
    while (iter.hasNext()) {
      Point2D p = iter.next();
      double dis = query.distanceSquaredTo(p);
      if (dis < min) {
        min = dis;
        near = p;
      }
    }
    return near;
  }

  private static <T> T requireNonNull(T obj, String message) {
    if (obj == null) {
      throw new IllegalArgumentException(message);
    }
    return obj;
  }
}
