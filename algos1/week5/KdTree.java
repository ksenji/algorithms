import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

  private enum Split {
    HORIZONTAL, VERTICAL;
  }

  private static final RectHV MAIN = new RectHV(0, 0, 1, 1);

  private Node root;

  public boolean isEmpty() {
    return size() == 0;
  }

  public int size() {
    return size(root);
  }

  private int size(Node n) {
    return (n != null) ? n.size : 0;
  }

  public void insert(Point2D p) {
    root = insert(root, MAIN, requireNonNull(p, "p cannot be null"), Split.VERTICAL);
  }

  public boolean contains(Point2D p) {
    return contains(root, requireNonNull(p, "p cannot be null"));
  }

  public void draw() {
    Stack<Node> stack = new Stack<>();
    push(stack, root);
    while (!stack.isEmpty()) {
      Node top = stack.pop();

      double radius = StdDraw.getPenRadius();
      StdDraw.setPenColor(Color.BLACK);
      StdDraw.setPenRadius(0.01d);
      top.key.draw();
      StdDraw.setPenRadius(radius);

      if (top.isVerticalSplit()) {
        StdDraw.setPenColor(Color.RED);
        StdDraw.line(top.key.x(), top.rect.ymin(), top.key.x(), top.rect.ymax());
      } else {
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.line(top.rect.xmin(), top.key.y(), top.rect.xmax(), top.key.y());
      }

      push(stack, top.right);
      push(stack, top.left);
    }
  }

  public Iterable<Point2D> range(RectHV rect) {
    requireNonNull(rect, "rect cannot be null");

    List<Point2D> points = new ArrayList<>();
    Stack<Node> stack = new Stack<>();
    push(stack, root);

    while (!stack.isEmpty()) {
      Node top = stack.pop();
      if (top.rect.intersects(rect)) {
        if (rect.contains(top.key)) {
          points.add(top.key);
        }
        push(stack, top.left);
        push(stack, top.right);
      }
    }

    return points::iterator;
  }

  public Point2D nearest(Point2D p) {
    requireNonNull(p, "p cannot be null");

    Point2D champion = null;
    double min = Double.POSITIVE_INFINITY;
    Stack<Node> stack = new Stack<>();
    push(stack, root);

    double x = p.x();
    double y = p.y();

    while (!stack.isEmpty()) {
      Node top = stack.pop();
      if (top.rect.distanceSquaredTo(p) < min) {
        double dist = top.key.distanceSquaredTo(p);
        if (dist < min) {
          min = dist;
          champion = top.key;
        }
        int cmp = top.isVerticalSplit() ? Double.compare(x, top.key.x()) : Double.compare(y, top.key.y());
        if (cmp < 0) {
          push(stack, top.right);
          push(stack, top.left);
        } else if (cmp > 0 || !top.key.equals(p)) {
          push(stack, top.left);
          push(stack, top.right);
        } else {
          champion = top.key;
          break;
        }
      }
    }

    return champion;
  }

  private static final class Node {
    /* We use key as the value as there are no delete semantics (i.e a Value of null is interpreted as delete in the standard BST) */
    private final Point2D key;

    private final RectHV rect;

    private final Split split;

    private Node left, right;

    private int size;

    Node(RectHV rect, Point2D key, Split split, int size) {
      this.rect = rect;
      this.key = key;
      this.split = split;
      this.size = size;
    }

    boolean isVerticalSplit() {
      return split == Split.VERTICAL;
    }
  }

  private Node insert(Node n, RectHV rect, Point2D p, Split orientation) {
    if (n == null) {
      return new Node(rect, p, orientation, 1);
    }

    boolean vs = n.isVerticalSplit();
    int cmp = vs ? Double.compare(p.x(), n.key.x()) : Double.compare(p.y(), n.key.y());
    RectHV nextRect;
    if (cmp < 0) {
      nextRect = (n.left != null) ? n.left.rect
          : new RectHV(rect.xmin(), rect.ymin(), vs ? n.key.x() : rect.xmax(), vs ? rect.ymax() : n.key.y());

      n.left = insert(n.left, nextRect, p, n.isVerticalSplit() ? Split.HORIZONTAL : Split.VERTICAL);
    } else if (cmp > 0 || !n.key.equals(p)) {
      nextRect = (n.right != null) ? n.right.rect
          : new RectHV(vs ? n.key.x() : rect.xmin(), vs ? rect.ymin() : n.key.y(), rect.xmax(), rect.ymax());
      
      n.right = insert(n.right, nextRect, p, n.isVerticalSplit() ? Split.HORIZONTAL : Split.VERTICAL);
    }
    n.size = 1 + size(n.left) + size(n.right);
    return n;
  }

  private boolean contains(Node n, Point2D p) {
    if (n == null) {
      return false;
    }

    int cmp = n.isVerticalSplit() ? Double.compare(p.x(), n.key.x()) : Double.compare(p.y(), n.key.y());
    if (cmp < 0) {
      return contains(n.left, p);
    } else if (cmp > 0 || !n.key.equals(p)) {
      return contains(n.right, p);
    }
    return true;
  }

  private void push(Stack<Node> stack, Node node) {
    if (node != null) {
      stack.push(node);
    }
  }

  private static <T> T requireNonNull(T obj, String message) {
    if (obj == null) {
      throw new IllegalArgumentException(message);
    }
    return obj;
  }
}
