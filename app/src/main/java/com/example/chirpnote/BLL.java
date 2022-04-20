package com.example.chirpnote;

/**
 * Better linked list
 * Because Java's linked List is A trociously S uffering S everely
 * BLL provides:
 * More power, simplicity, and intuition to the developer without muddle
 *
 * @param <T> generic type
 */
public class BLL<T> {

    /**
     * Nested Node class for Better Linked List
     * @param <A> generic type
     */
    private static class Node<A> {
        public A type;
        public Node<A> next;
        public Node<A> previous;

        public Node(A type) {
            this.type = type;
            next = null;
            previous = null;
        }
    }

    private Node<T> begin;
    private Node<T> current;
    private Node<T> end;

    public BLL() {
        current = null;
        begin = null;
        end = null;
    }

    /**
     * Add node at the end of BLL
     * @param type object to add
     */
    public void add(T type) {
        if (end == null) {
            begin = new Node<>(type);
            current = begin;
            end = current;
        } else {
            Node<T> temp = new Node<>(type);
            temp.previous = end;
            end.next = temp;
            end = temp;
        }
    }

    /**
     * Add node at the beginning of BLL
     * @param type object to add
     */
    public void addBegin(T type) {
        if (begin == null) {
            begin = new Node<>(type);
            current = begin;
            end = current;
        } else {
            Node<T> temp = new Node<>(type);
            temp.next = begin;
            begin.previous = temp;
            begin = temp;
        }
    }

    /**
     * @return true if current has a next node
     */
    public boolean hasNext() {
        return current.next != null;
    }

    /**
     * @return true if current has a previous node
     */
    public boolean hasPrevious() {
        return current.previous != null;
    }

    /**
     * Go to next node
     */
    public void next() {
        if (hasNext()) {
            current = current.next;
        } else {
            throw new NullPointerException("hasNext is null");
        }
    }

    /**
     * Go to previous node
     */
    public void previous() {
        if (hasPrevious()) {
            current = current.previous;
        } else {
            throw new NullPointerException("hasPrevious is null");
        }
    }

    /**
     * Insert node before current node
     * @param type object to insert
     */
    public void insertBefore(T type) {
        if (current == null) {
            begin = new Node<>(type);
            current = begin;
            end = current;
        } else {
            Node<T> temp = new Node<>(type);
            temp.previous = current.previous;
            temp.next = current;
            current.previous = temp;
            if (begin == current) begin = temp;
        }
    }

    /**
     * Insert node after current node
     * @param type object to insert
     */
    public void insertAfter(T type) {
        if (current == null) {
            begin = new Node<>(type);
            current = begin;
            end = current;
        } else {
            Node<T> temp = new Node<>(type);
            temp.next = current.next;
            temp.previous = current;
            current.next = temp;
            if (end == current) end = temp;
        }
    }

    /**
     * Remove the current node
     * The previous node is the new current node
     */
    public void removeThenBefore() {
        if (current == null) throw new NullPointerException("Can't remove from empty list");
        else {
            int option = 3;
            if (hasPrevious()) current.previous.next = current.next;
            else option-=1;
            if (hasNext()) current.next.previous = current.previous;
            else option-=2;

            if (option == 2) {
                current = current.next;
                begin = current;
            } else {
                current = current.previous;
                if (option < 3) {
                    end = current;
                    if (option == 0) begin = current;
                }
            }
        }
    }

    /**
     * Remove the current node
     * The next node is the new current node
     */
    public void removeThenAfter() {
        if (current == null) throw new NullPointerException("Can't remove from empty list");
        else {
            int option = 3;
            if (hasPrevious()) current.previous.next = current.next;
            else option-=1;
            if (hasNext()) current.next.previous = current.previous;
            else option-=2;

            if (option == 1) {
                current = current.previous;
                end = current;
            } else {
                current = current.next;
                if (option < 3) {
                    begin = current;
                    if (option == 0) end = current;
                }
            }
        }
    }

    /**
     * Remove node at beginning of BLL
     */
    public void removeBegin() {
        if (begin == null) throw new NullPointerException("Can't remove from empty list");
        else {
            if (current == begin) current = current.next;
            if (end == begin) end = end.next;
            begin = begin.next;
        }
    }

    /**
     * Remove node at end of BLL
     */
    public void removeEnd() {
        if (end == null) throw new NullPointerException("Can't remove from empty list");
        else {
            if (current == end) current = current.previous;
            if (begin == end) begin = begin.next;
            end = end.previous;
        }
    }

    /**
     * Retrieve the current node
     * @return T object at current node
     */
    public T get() {
        return current.type;
    }

    /**
     * Retrieve next node without going to next node
     * @return T object from current's next node
     */
    public T peekNext() {
        return current.next.type;
    }

    /**
     * Retrieve previous node without going to previous node
     * @return T object from current's previous node
     */
    public T peekPrevious() {
        return current.previous.type;
    }

}
