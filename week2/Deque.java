package test;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

  private Node<Item> first;

  private Node<Item> last;

  private int size;

  public boolean isEmpty() {
    return first == null;
  }

  public int size() {
    return size;
  }

  public void addFirst(Item item) {
    checkNotNull(item);
    Node<Item> node = new Node<>(item);
    if (first == null) {
      first = node;
    } else {
      Node<Item> old = first;
      first = node;
      first.next = old;
      old.prev = first;
    }

    if (last == null) {
      last = first;
    }
    size++;
  }

  public void addLast(Item item) {
    checkNotNull(item);
    Node<Item> node = new Node<>(item);
    if (last == null) {
      last = node;
    } else {
      Node<Item> old = last;
      old.next = node;
      last = node;
      last.prev = old;
    }
    if (first == null) {
      first = last;
    }
    size++;
  }

  public Item removeFirst() {
    checkEmpty();
    Item item = first.e;
    Node<Item> next = first.next;
    if (next != null) {
      first.next = null;
      next.prev = null;
      first = next;
    } else {
      first = null;
      last = null;
    }
    size--;
    return item;
  }

  public Item removeLast() {
    checkEmpty();
    Item item = last.e;
    Node<Item> prev = last.prev;
    if (prev != null) {
      last.prev = null;
      prev.next = null;
      last = prev;
    } else {
      first = null;
      last = null;
    }
    size--;
    return item;
  }

  @Override
  public Iterator<Item> iterator() {
    return new IteratorImpl<>(first);
  }

  private void checkNotNull(Item item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
  }

  private void checkEmpty() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
  }

  private static final class IteratorImpl<E> implements Iterator<E> {

    private Node<E> first;

    IteratorImpl(Node<E> first) {
      this.first = first;
    }

    @Override
    public boolean hasNext() {
      return first != null;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      E e = first.e;
      first = first.next;
      return e;
    }
  }

  private static final class Node<E> {
    final E e;

    Node<E> next;

    Node<E> prev;

    Node(E e) {
      this.e = e;
    }
  }
}
