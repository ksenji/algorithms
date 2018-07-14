package test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {

  private int size;

  private Object[] elements;

  public RandomizedQueue() {
    elements = new Object[1];
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return size;
  }

  public void enqueue(Item item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    if (size == elements.length) {
      resize(new Object[elements.length << 1]);
    }
    elements[size++] = item;
  }

  public Item dequeue() {
    IndexAndElement<Item> item = random();
    elements[item.pos] = elements[size - 1];
    if (size < elements.length / 4) {
      resize(new Object[elements.length >> 1]);
    }
    elements[--size] = null;
    return item.e;
  }

  private void resize(Object[] temp) {
    System.arraycopy(elements, 0, temp, 0, size);
    elements = temp;
  }

  public Item sample() {
    return random().e;
  }

  private IndexAndElement<Item> random() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    //@SuppressWarnings("unchecked")
    int idx = StdRandom.uniform(size);
    Item e = (Item) elements[idx];
    return new IndexAndElement<>(idx, e);
  }

  @Override
  public Iterator<Item> iterator() {
    int len = size;
    Object[] copy = new Object[len];
    System.arraycopy(elements, 0, copy, 0, len);
    StdRandom.shuffle(copy);
    return new IteratorImpl<>(copy);
  }

  private static final class IteratorImpl<E> implements Iterator<E> {

    int pos;

    final Object[] elements;

    private IteratorImpl(Object[] elements) {
      this.elements = elements;
    }

    @Override
    public boolean hasNext() {
      return pos < elements.length;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      //@SuppressWarnings("unchecked")
      E e = (E) elements[pos++];
      return e;
    }
  }

  private static class IndexAndElement<E> {
    final E e;

    final int pos;

    IndexAndElement(int pos, E e) {
      this.pos = pos;
      this.e = e;
    }
  }
}
