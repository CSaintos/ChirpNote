package com.example.chirpnote;

import android.util.Log;

/**
 * Better linked list
 * Because Java's linked List is A trociously S uffering S everely
 * BLL provides:
 * More power, simplicity, and intuition to the developer without muddle
 *
 * @param <T> generic type
 */
public class BLL<T> {

    protected Node begin;
    protected Node end;
    protected int size;

    /**
     * Nested Node class for Better Linked List
     */
    protected class Node {
        public Node next;
        public Node previous;
        public T type;

        public Node(T type) {
            this.type = type;
            next = null;
            previous = null;
        }
    }

    public BLL() {
        begin = null;
        end = null;
        size = 0;
    }

    /**
     * Add node at the end of BLL
     * @param type object to add
     */
    public void appendEnd(T type) {
        if (end == null) {
            begin = new Node(type);
            end = begin;
        } else {
            Node temp = new Node(type);
            temp.previous = end;
            end.next = temp;
            end = temp;
        }
        size++;
    }

    /**
     * Add node at the beginning of BLL
     * @param type object to add
     */
    public void appendBegin(T type) {
        if (begin == null) {
            begin = new Node(type);
            end = begin;
        } else {
            Node temp = new Node(type);
            temp.next = begin;
            begin.previous = temp;
            begin = temp;
        }
        size++;
    }


    /**
     * Set object as begin's data
     * @param type object to set
     */
    public void setBegin(T type) {
        if (begin != null) begin.type = type;
        else throw new NullPointerException("Can't set begin node on empty list");
    }

    /**
     * Set object as end's data
     * @param type object to set
     */
    public void setEnd(T type) {
        if (end != null) end.type = type;
        else throw new NullPointerException("Can't set end node on empty list");
    }

    /**
     * Remove node at beginning of BLL
     */
    public void removeBegin() {
        if (begin == null) throw new NullPointerException("Can't remove from empty list");
        else {
            //if (current == begin) current = current.next;
            if (end == begin) end = end.next;
            begin = begin.next;
            size--;
        }
    }

    /**
     * Remove node at end of BLL
     */
    public void removeEnd() {
        if (end == null) throw new NullPointerException("Can't remove from empty list");
        else {
            //if (current == end) current = current.previous;
            if (begin == end) begin = begin.next;
            end = end.previous;
            size--;
        }
    }

    /**
     * Retrieve the begin node's data
     * @return T object at begin node
     */
    public T getBegin() {
        return begin.type;
    }

    /**
     * Retrieve the end node's data
     * @return T object at end node
     */
    public T getEnd() {
        return end.type;
    }

    public int size() {
        return size;
    }

    /**
     * Nested ListIterator class for Better Linked List
     */
    public class ListIterator {
        private Node current;

        private ListIterator(Node node) {
            current = node;
        }

        /**
         * Sets current to begin node
         */
        public void goToBegin() {
            current = begin;
        }

        /**
         * Sets current to end node
         */
        public void goToEnd() {
            current = end;
        }

        /**
         * Add node at the end of BLL
         * @param type object to add
         */
        public void appendEnd(T type) {
            if (end == null) {
                begin = new Node(type);
                current = begin;
                end = current;
            } else {
                Node temp = new Node(type);
                temp.previous = end;
                end.next = temp;
                end = temp;
            }
            size++;
        }

        /**
         * Add node at the beginning of BLL
         * @param type object to add
         */
        public void appendBegin(T type) {
            if (begin == null) {
                begin = new Node(type);
                current = begin;
                end = current;
            } else {
                Node temp = new Node(type);
                temp.next = begin;
                begin.previous = temp;
                begin = temp;
            }
            size++;
        }

        /**
         * Set object as current's data
         * @param type object to set
         */
        public void set(T type) {
            if (current != null) current.type = type;
            else throw new NullPointerException("Can't set current node on empty list");
        }

        /**
         * Set object as begin's data
         * @param type object to set
         */
        public void setBegin(T type) {
            if (begin != null) begin.type = type;
            else throw new NullPointerException("Can't set begin node on empty list");
        }

        /**
         * Set object as end's data
         * @param type object to set
         */
        public void setEnd(T type) {
            if (end != null) end.type = type;
            else throw new NullPointerException("Can't set end node on empty list");
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
                begin = new Node(type);
                current = begin;
                end = current;
            } else {
                Node temp = new Node(type);
                if (current.previous != null) {
                    temp.previous = current.previous;
                    current.previous.next = temp;
                } else {
                    temp.previous = null; //1
                }
                temp.previous = current.previous; //1 redundant
                temp.next = current;
                current.previous = temp;
                if (begin == current) begin = temp;
            }
            size++;
        }

        /**
         * Insert node after current node
         * @param type object to insert
         */
        public void insertAfter(T type) {
            if (current == null) {
                begin = new Node(type);
                current = begin;
                end = current;
            } else {
                Node temp = new Node(type);
                if (current.next != null) {
                    temp.next = current.next;
                    current.next.previous = temp;
                } else {
                    temp.next = null;
                }
                temp.previous = current;
                current.next = temp;
                if (end == current) end = temp;

                //if (temp.previous != null) Log.d("insertAfter previous", (temp.previous.type.toString()));
                //if (temp.next != null) Log.d("insertAfter next", (temp.next.type.toString()));
                //if (temp.next == null || temp.previous == null) Log.d("insertAfter", "null");
            }
            size++;
        }

        /**
         * Remove the current node
         * The previous node is the new current node
         *
         * TODO do a fullproof test on this and removeThenAfter()
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
                size--;
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
                size--;
            }
        }

        /**
         * Retrieve the current node's data
         * @return T object at current node
         */
        public T get() {
            if (current == null) return null;
            return current.type;
        }

        /**
         * Retrieve next node's data without going to next node
         * @return T object from current's next node
         */
        public T peekNext() {
            if (hasNext()) return current.next.type;
            return null;
        }

        /**
         * Retrieve previous node's data without going to previous node
         * @return T object from current's previous node
         */
        public T peekPrevious() {
            if (hasPrevious()) return current.previous.type;
            return null;
        }

        /**
         * Retrieve the begin node's data
         * @return T object at begin node
         */
        public T getBegin() {
            if (begin == null) return null;
            return begin.type;
        }

        /**
         * Retrieve the end node's data
         * @return T object at end node
         */
        public T getEnd() {
            if (end == null) return null;
            return end.type;
        }
    }

    public ListIterator listIterator() {
        return new ListIterator(begin);
    }

}
