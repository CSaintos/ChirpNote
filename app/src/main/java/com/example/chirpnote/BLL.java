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

    protected Node<T> begin = null;
    //protected Node<T> current = null;
    protected Node<T> end = null;

    /**
     * Nested Node class for Better Linked List
     * @param <A> generic type
     */
    protected class Node<A> {
        public A type;
        public Node<A> next;
        public Node<A> previous;

        public Node(A type) {
            this.type = type;
            next = null;
            previous = null;
        }
    }

    public BLL() {
        //current = null;
        begin = null;
        end = null;
    }

    /**
     * Add node at the end of BLL
     * @param type object to add
     */
    public void appendEnd(T type) {
        if (end == null) {
            begin = new Node<>(type);
            end = begin;
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
    public void appendBegin(T type) {
        if (begin == null) {
            begin = new Node<>(type);
            end = begin;
        } else {
            Node<T> temp = new Node<>(type);
            temp.next = begin;
            begin.previous = temp;
            begin = temp;
        }
    }

    /**
     * Set object as current's data
     * @param type object to set
     */
    /*
    public void set(T type) {
        if (current != null) current.type = type;
        else throw new NullPointerException("Can't set current node on empty list");
    }*/

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
    /*
    public boolean hasNext() {
        return current.next != null;
    }
     */

    /**
     * @return true if current has a previous node
     */
    /*
    public boolean hasPrevious() {
        return current.previous != null;
    }
     */

    /**
     * Go to next node
     */
    /*
    public void next() {
        if (hasNext()) {
            current = current.next;
        } else {
            throw new NullPointerException("hasNext is null");
        }
    }
     */

    /**
     * Go to previous node
     */
    /*
    public void previous() {
        if (hasPrevious()) {
            current = current.previous;
        } else {
            throw new NullPointerException("hasPrevious is null");
        }
    }
     */

    /**
     * Insert node before current node
     * @param type object to insert
     */
    /*
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
     */

    /**
     * Insert node after current node
     * @param type object to insert
     */
    /*
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
     */

    /**
     * Remove the current node
     * The previous node is the new current node
     */
    /*
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
     */

    /**
     * Remove the current node
     * The next node is the new current node
     */
    /*
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
     */

    /**
     * Remove node at beginning of BLL
     */
    public void removeBegin() {
        if (begin == null) throw new NullPointerException("Can't remove from empty list");
        else {
            //if (current == begin) current = current.next;
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
            //if (current == end) current = current.previous;
            if (begin == end) begin = begin.next;
            end = end.previous;
        }
    }

    /**
     * Retrieve the current node's data
     * @return T object at current node
     */
    /*
    public T get() {
        return current.type;
    }
     */

    /**
     * Retrieve next node's data without going to next node
     * @return T object from current's next node
     */
    /*
    public T peekNext() {
        return current.next.type;
    }
     */

    /**
     * Retrieve previous node's data without going to previous node
     * @return T object from current's previous node
     */
    /*
    public T peekPrevious() {
        return current.previous.type;
    }
     */

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

    /**
     * Nested ListIterator class for Better Linked List
     * @param <B> generic type
     */
    public class ListIterator<B> {
        private Node<B> currentT;

        private ListIterator(Node<B> node) {
            currentT = node;
        }

        /**
         * Add node at the end of BLL
         * @param type object to add
         */
        public void appendEnd(B type) {
            if (end == null) {
                begin = new Node<>((T)type);
                currentT = (Node<B>)begin;
                end = (Node<T>)currentT;
            } else {
                Node<T> temp = new Node<>((T)type);
                temp.previous = end;
                end.next = temp;
                end = temp;
            }
        }

        /**
         * Add node at the beginning of BLL
         * @param type object to add
         */
        public void appendBegin(B type) {
            if (begin == null) {
                begin = new Node<>((T)type);
                currentT = (Node<B>)begin;
                end = (Node<T>)currentT;
            } else {
                Node<T> temp = new Node<>((T)type);
                temp.next = begin;
                begin.previous = temp;
                begin = temp;
            }
        }

        /**
         * Set object as current's data
         * @param type object to set
         */
        public void set(B type) {
            if (currentT != null) currentT.type = type;
            else throw new NullPointerException("Can't set current node on empty list");
        }

        /**
         * Set object as begin's data
         * @param type object to set
         */
        public void setBegin(B type) {
            if (begin != null) begin.type = (T) type;
            else throw new NullPointerException("Can't set begin node on empty list");
        }

        /**
         * Set object as end's data
         * @param type object to set
         */
        public void setEnd(B type) {
            if (end != null) end.type = (T) type;
            else throw new NullPointerException("Can't set end node on empty list");
        }

        /**
         * @return true if current has a next node
         */
        public boolean hasNext() {
            return currentT.next != null;
        }

        /**
         * @return true if current has a previous node
         */
        public boolean hasPrevious() {
            return currentT.previous != null;
        }

        /**
         * Go to next node
         */
        public void next() {
            if (hasNext()) {
                currentT = currentT.next;
            } else {
                throw new NullPointerException("hasNext is null");
            }
        }

        /**
         * Go to previous node
         */
        public void previous() {
            if (hasPrevious()) {
                currentT = currentT.previous;
            } else {
                throw new NullPointerException("hasPrevious is null");
            }
        }

        /**
         * Insert node before current node
         * @param type object to insert
         */
        public void insertBefore(B type) {
            if (currentT == null) {
                begin = new Node<>((T)type);
                currentT = (Node<B>)begin;
                end = (Node<T>)currentT;
            } else {
                Node<T> temp = new Node<>((T)type);
                temp.previous = (Node<T>)currentT.previous;
                temp.next = (Node<T>)currentT;
                currentT.previous = (Node<B>)temp;
                if (begin == (Node<T>)currentT) begin = temp;
            }
        }

        /**
         * Insert node after current node
         * @param type object to insert
         */
        public void insertAfter(B type) {
            if (currentT == null) {
                begin = new Node<>((T)type);
                currentT = (Node<B>)begin;
                end = (Node<T>)currentT;
            } else {
                Node<T> temp = new Node<>((T)type);
                temp.next = (Node<T>)currentT.next;
                temp.previous = (Node<T>)currentT;
                currentT.next = (Node<B>)temp;
                if (end == (Node<T>)currentT) end = temp;
            }
        }

        /**
         * Remove the current node
         * The previous node is the new current node
         */
        public void removeThenBefore() {
            if (currentT == null) throw new NullPointerException("Can't remove from empty list");
            else {
                int option = 3;
                if (hasPrevious()) currentT.previous.next = currentT.next;
                else option-=1;
                if (hasNext()) currentT.next.previous = currentT.previous;
                else option-=2;

                if (option == 2) {
                    currentT = currentT.next;
                    begin = (Node<T>)currentT;
                } else {
                    currentT = currentT.previous;
                    if (option < 3) {
                        end = (Node<T>)currentT;
                        if (option == 0) begin = (Node<T>)currentT;
                    }
                }
            }
        }

        /**
         * Remove the current node
         * The next node is the new current node
         */
        public void removeThenAfter() {
            if (currentT == null) throw new NullPointerException("Can't remove from empty list");
            else {
                int option = 3;
                if (hasPrevious()) currentT.previous.next = currentT.next;
                else option-=1;
                if (hasNext()) currentT.next.previous = currentT.previous;
                else option-=2;

                if (option == 1) {
                    currentT = currentT.previous;
                    end = (Node<T>)currentT;
                } else {
                    currentT = currentT.next;
                    if (option < 3) {
                        begin = (Node<T>)currentT;
                        if (option == 0) end = (Node<T>)currentT;
                    }
                }
            }
        }

        /**
         * Retrieve the current node's data
         * @return T object at current node
         */
        public B get() {
            return currentT.type;
        }

        /**
         * Retrieve next node's data without going to next node
         * @return T object from current's next node
         */
        public B peekNext() {
            return currentT.next.type;
        }

        /**
         * Retrieve previous node's data without going to previous node
         * @return T object from current's previous node
         */
        public B peekPrevious() {
            return currentT.previous.type;
        }
    }

    public ListIterator<T> listIterator() {
        return new ListIterator<>(begin);
    }

}
