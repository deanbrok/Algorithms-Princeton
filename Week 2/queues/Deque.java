/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first, last;
    private int size;

    private class Node {
        Node previous;
        Item item;
        Node next;
    }

    // construct an empty deque
    public Deque() {
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();

        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;


        if (isEmpty()) last = first;
        else oldFirst.previous = first;

        ++size;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();

        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.previous = oldLast;
        last.next = null;

        if (isEmpty()) first = last;
        else oldLast.next = last;

        ++size;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();

        Item item = first.item;
        first = first.next;
        if (first != null) first.previous = null;
        --size;
        if (isEmpty()) last = null;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();

        Item item = last.item;
        last = last.previous;
        if (last != null) last.next = null;
        --size;
        if (isEmpty()) first = null;
        return item;

    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() { return new DequeIterator(); }

    private class DequeIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() { return current != null; }

        public void remove() { throw new UnsupportedOperationException(); }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> stringDeque = new Deque<>();

        stringDeque.addFirst("Hi");
        stringDeque.addLast("Hello");

        for (String s: stringDeque) {
            StdOut.println(s);
        }

        StdOut.println("\nRemove operations:");

        StdOut.println(stringDeque.removeLast());
        StdOut.println(stringDeque.removeFirst());
        StdOut.println("isEmpty(): " + stringDeque.isEmpty());
        stringDeque.addFirst("Wassup");
        stringDeque.addLast("Yup");

        StdOut.println("\nAfter complete removal:");

        for (String s: stringDeque) {
            StdOut.println(s);
        }

    }

}
