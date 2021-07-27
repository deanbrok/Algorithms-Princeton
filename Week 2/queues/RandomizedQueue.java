/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int size;
    private Item[] items;

    // construct an empty randomized queue
    public RandomizedQueue() {
        items  = (Item[]) new Object[2];
    }


    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];

        for (int i = 0; i < size; i++)
            copy[i] = items[i];

        items = copy;
    }
    // is the randomized queue empty?
    public boolean isEmpty() { return size == 0; }

    // return the number of items on the randomized queue
    public int size() { return size; }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();
        if (size == items.length) resize(items.length * 2);
        items[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        if (size > 0 && size == items.length / 4) resize(items.length / 2);

        int randomIndex = StdRandom.uniform(size);
        Item item = items[randomIndex];
        items[randomIndex] = items[--size];
        items[size] = null;

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();

        int randomIndex = StdRandom.uniform(size);
        return items[randomIndex];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() { return new RQueueIterator();}

    private class RQueueIterator implements Iterator<Item> {
        private int i;
        private Item[] iItems;
        public RQueueIterator() {
            i = 0;
            iItems = (Item[]) new Object[size];

            for (int j = 0; j < size; j++) {
                iItems[j] = items[j];
            }

            StdRandom.shuffle(iItems);
        }

        public boolean hasNext() {
            return i < size;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return iItems[i++];
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> rStrings = new RandomizedQueue<>();

        rStrings.enqueue("Sup");
        rStrings.enqueue("Hi");
        rStrings.enqueue("Coffee?");
        rStrings.enqueue("Sure");
        rStrings.enqueue("Where?");
        rStrings.enqueue("CoffeeBuck?");
        rStrings.enqueue("Okay!");

        StdOut.println("Foreach Loop 1: ");
        for (String s: rStrings) {
            StdOut.println(s);
        }

        StdOut.println();
        StdOut.println("Foreach Loop 2: ");
        for (String s: rStrings) {
            StdOut.println(s);
        }

        StdOut.println();

        StdOut.println("Sample:");
        StdOut.println(rStrings.sample());
        StdOut.println(rStrings.sample());

        StdOut.println();
        StdOut.println("Dequeue All: ");
        StdOut.println(rStrings.size());

        int currentSize = rStrings.size();
        for (int i = 0; i < currentSize; i++) {
            StdOut.println(rStrings.dequeue());
        }

        StdOut.println("isEmpty(): " + rStrings.isEmpty());


    }
}
