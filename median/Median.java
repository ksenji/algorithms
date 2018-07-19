import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Median {

  private final PriorityQueue<Integer> max;

  private final PriorityQueue<Integer> min;

  public Median() {
    this.max = new PriorityQueue<>((a, b) -> b - a);
    this.min = new PriorityQueue<>(Integer::compare);
  }

  public int median() {
    if (size() == 0) {
      throw new NoSuchElementException();
    }
    return max.peek();
  }

  public void add(int N) {
    if (max.isEmpty()) {
      max.add(N);
    } else {
      if (N <= max.peek()) {
        max.add(N);
      } else {
        min.add(N);
      }
      balance();
    }
  }

  private void balance() {
    if (!balanced()) {
      if (max.size() > min.size()) {
        min.add(max.poll());
      } else {
        max.add(min.poll());
      }
      balance();
    }
  }

  private boolean balanced() {
    int diff = max.size() - min.size();
    return diff == 0 || diff == 1;
  }

  public int size() {
    return max.size() + min.size();
  }

  public static void main(String[] args) throws IOException {
    int total = 0;
    int size = 0;
    Median m = new Median();
    try (Scanner scanner = new Scanner(Paths.get(args[0]), StandardCharsets.UTF_8.name())) {
      while (scanner.hasNext()) {
        m.add(scanner.nextInt());
        total += m.median();
        size++;
      }
    }
    System.out.println(total % size); //Prints 1213
  }
}
