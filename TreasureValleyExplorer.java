import java.util.*;

/**
 * A convenient class that stores a pair of integers.
 * DO NOT MODIFY THIS CLASS.
 */

class IntPair {
    // Make the fields final to ensure they cannot be changed after initialization
    public final int first;
    public final int second;

    public IntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IntPair other = (IntPair) obj;
        return first == other.first && second == other.second;
    }

    @Override
    public int hashCode() {
        return 31 * first + second;
    }
}

/**
 * TreasureValleyExplorer class operates on a landscape of Numerica,
 * selectively modifying the most and least valuable valleys of a specified
 * depth.
 *
 * DO NOT MODIFY THE SIGNATURE OF THE METHODS PROVIDED IN THIS CLASS.
 * You are encouraged to add methods and variables in the class as needed.
 *
 * @author Saloni Shah
 */
public class TreasureValleyExplorer {

    private class Node {
        int height, value;
        Node prev, next;

        public Node(int height, int value) {
            this.height = height;
            this.value = value;
        }
    }

    private class Valley {
        int depth;
        Node node;
        Valley valleyPrev, valleyNext;

        public Valley(int depth, Node node) {
            this.depth = depth;
            this.node = node;
        }
    }

    private Node head, tail;
    private Valley valleyHead, valleyTail;
    private Map<Integer, TreeSet<Valley>> valleysAtDepth;
    private int[] heights, values;

    /**
     * Constructor to initialize the TreasureValleyExplorer with the given heights
     * and values
     * of points in Numerica.
     *
     * @param heights An array of distinct integers representing the heights of
     *                points in the landscape.
     * @param values  An array of distinct integers representing the treasure value
     *                of points in the landscape.
     */
    public TreasureValleyExplorer(int[] heights, int[] values) {
        this.heights = heights;
        this.values = values;
        valleysAtDepth = new TreeMap<>();
        // initialize the landscape
        for(int i = heights.length - 1; i >= 0; i--) {
            Node newNode = new Node(heights[i], values[i]);
            insertAtHead(newNode);
        }
        // initialize the valleys
        initializeValleys();
    }

    /**
     * Insert a node at the head.
     *
     * @param node the node to be inserted
     */
    private void insertAtHead(Node node) {
        if(head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    private void initializeValleys() {
        Node current = head;
        int prevDepth = 0;
        int index = 0;

        while (current != null) {
            int depth = 0;

            int height = heights[index];
            int prevHeight = Integer.MIN_VALUE;
            if (index > 0) {
                prevHeight = heights[index - 1];
            }

            // reset depth at peaks and track depth based on the formula given
            if (isPeak(current)) {
                depth = 0;
                //inDescent = true;
            } else if (height < prevHeight) {
                depth = prevDepth + 1;
            }

            prevDepth = depth;

            // store the valley
            if (isValley(current)) {
                Valley valley = new Valley(depth, current);
                insertValley(valley, depth);
            }

            current = current.next;
            index++;
        }
    }

    /**
     * Inserts the valley into the valley doubly linked list.
     *
     * @param valley the valley to insert
     * @param depth the depth to insert at
     */
    private void insertValley(Valley valley, int depth) {
        // Insert into the valley doubly linked list
        if (valleyHead == null) {
            valleyHead = valleyTail = valley;
        } else {
            valley.valleyNext = valleyTail.valleyNext;
            valley.valleyPrev = valleyTail;
            valleyTail.valleyNext = valley;
            valleyTail = valley;
        }

        // Insert into the sorted set for its depth
        addValleyToDepth(depth, valley);
    }

    /**
     * Adds a valley to the sorted set for its particular depth.
     * @param depth the depth to insert the valley at
     * @param valley the valley to insert
     */
    private void addValleyToDepth(int depth, Valley valley) {
        valleysAtDepth.putIfAbsent(depth, new TreeSet<>(Comparator.comparingInt(v -> v.node.value)));
        valleysAtDepth.get(depth).add(valley);
    }

    /**
     * Checks whether the given node is a valley using the conditions provided.
     *
     * @param node the node to check conditions for
     * @return true if it is a valley, false if not
     */
    private boolean isValley(Node node) {
        if (node.prev == null && node.next != null) {
            return node.height < node.next.height;
        }

        if (node.next == null && node.prev != null) {
            return node.height < node.prev.height;
        }

        if (node.prev != null && node.next != null) {
            return node.height < node.prev.height && node.height < node.next.height;
        }

        return true;
    }

    /**
     * Checks whether the given node is a peak using the conditions provided.
     *
     * @param node the node to check conditions for
     * @return true if it is a peak, false if not
     */
    private boolean isPeak(Node node) {
        if (node.prev == null && node.next != null) {
            return node.height > node.next.height;
        }

        if (node.next == null && node.prev != null) {
            return node.height > node.prev.height;
        }

        if (node.prev != null && node.next != null) {
            return node.height > node.prev.height && node.height > node.next.height;
        }

        return true;
    }

    /**
     * Checks if the entire landscape is excavated (i.e., there are no points
     * left).
     *
     * @return true if the landscape is empty, false otherwise.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * A method to insert a new landform prior to the most valuable valley of the
     * specified depth
     *
     * @param height The height of the new landform
     * @param value  The treasure value of the new landform
     * @param depth  The depth of the valley we wish to insert at
     *
     * @return true if the insertion is successful, false otherwise
     */
    public boolean insertAtMostValuableValley(int height, int value, int depth) {
        TreeSet<Valley> valleys = valleysAtDepth.get(depth);
        // need to check if that depth even exists
        if (valleys == null || valleys.isEmpty()) {
            return false;
        }
        Node newNode = new Node(height, value);
        Valley mostValuableValley = valleys.last(); // returns the most valuable valley @ that depth
        // do the insertion
        newNode.next = mostValuableValley.node;
        newNode.prev = mostValuableValley.node.prev;

        if(mostValuableValley.node.prev != null) {
            mostValuableValley.node.prev.next = newNode;
        } else {
            insertAtHead(newNode);
        }

        mostValuableValley.node.prev = newNode;

        // check various conditions on the node
        // condition 1: is the node we're inserting a valley?
        if(isValley(newNode)) {
            // remove and replace the LVV in the valley list with the node
            Valley newNodeValley = new Valley(depth, newNode);
            // remove the prev valley from the map
            TreeSet<Valley> oldSet = valleysAtDepth.get(mostValuableValley.depth);
            if (oldSet != null) {
                oldSet.remove(mostValuableValley);
                // if the set becomes empty after removal, remove the key from the map
                if (oldSet.isEmpty()) {
                    valleysAtDepth.remove(mostValuableValley.depth);
                }
            }
            // add this new node to that set
            addValleyToDepth(depth, newNodeValley);
            mostValuableValley.node = newNode;
        }
        // condition 2
        // is the node that we're inserting a peak?
        else if(isPeak(newNode)) {
            // condition 2a: is the node before this one now a valley?
            // if yes, then the depth of the current node becomes 0
            // and the prev node needs to be added to the valley stuff
            if(newNode.prev != null && isValley(newNode.prev)) {
                // need a check for if the prev node is the head:
                Valley prevNodeValley = new Valley(mostValuableValley.depth - 1, newNode.prev);
                addValleyToDepth(prevNodeValley.depth, prevNodeValley);
                insertValley(prevNodeValley, prevNodeValley.depth);
            }
            // most valuable valley's depth will ALWAYS "reset" to 1
            if(mostValuableValley.depth != 1) {
                TreeSet<Valley> oldSet = valleysAtDepth.get(mostValuableValley.depth);
                if (oldSet != null) {
                    oldSet.remove(mostValuableValley);
                    // if the set becomes empty after removal, remove the key from the map
                    if (oldSet.isEmpty()) {
                        valleysAtDepth.remove(mostValuableValley.depth);
                    }
                }
                // always the case because it will become the first downward node in a descend point
                mostValuableValley.depth = 1;
                addValleyToDepth(mostValuableValley.depth, mostValuableValley);
            }
        }
        // it's just another point on the descent
        else if(!isValley(newNode) && !isPeak(newNode)) {
            // remove the LVV from the valley map + update it
            TreeSet<Valley> oldSet = valleysAtDepth.get(mostValuableValley.depth);
            if (oldSet != null) {
                oldSet.remove(mostValuableValley);
                // if the set becomes empty after removal, remove the key from the map
                if (oldSet.isEmpty()) {
                    valleysAtDepth.remove(mostValuableValley.depth);
                }
            }
            // increase the depth of the LVV
            mostValuableValley.depth++;
            // add this new node to that set
            addValleyToDepth(mostValuableValley.depth, mostValuableValley);
        }

        return true;
    }

    /**
     * A method to insert a new landform prior to the least valuable valley of the
     * specified depth
     *
     * @param height The height of the new landform
     * @param value  The treasure value of the new landform
     * @param depth  The depth of the valley we wish to insert at
     *
     * @return true if the insertion is successful, false otherwise
     */
    public boolean insertAtLeastValuableValley(int height, int value, int depth) {
        TreeSet<Valley> valleys = valleysAtDepth.get(depth);
        // need to check if that depth even exists
        if (valleys == null || valleys.isEmpty()) {
            return false;
        }
        Node newNode = new Node(height, value);
        Valley leastValuableValley = valleys.first(); // returns the least valuable valley @ that depth
        // do the insertion
        newNode.next = leastValuableValley.node;
        newNode.prev = leastValuableValley.node.prev;

        if(leastValuableValley.node.prev != null) {
            leastValuableValley.node.prev.next = newNode;
        } else {
            insertAtHead(newNode);
        }

        leastValuableValley.node.prev = newNode;

        // check various conditions on the node
        // condition 1: is the node we're inserting a valley?
        if(isValley(newNode)) {
            // remove and replace the LVV in the valley list with the node
            Valley newNodeValley = new Valley(depth, newNode);
            // remove the prev valley from the map
            TreeSet<Valley> oldSet = valleysAtDepth.get(leastValuableValley.depth);
            if (oldSet != null) {
                oldSet.remove(leastValuableValley);
                // if the set becomes empty after removal, remove the key from the map
                if (oldSet.isEmpty()) {
                    valleysAtDepth.remove(leastValuableValley.depth);
                }
            }
            // add this new node to that set
            addValleyToDepth(depth, newNodeValley);
            leastValuableValley.node = newNodeValley.node;
        }
        // condition 2
        // is the node that we're inserting a peak?
        else if(isPeak(newNode)) {
            // condition 2a: is the node before this one now a valley?
            // if yes, then the depth of the current node becomes 0
            // and the prev node needs to be added to the valley stuff
            if(newNode.prev != null && isValley(newNode.prev)) {
                // need a check for if the prev node is the head:
                Valley prevNodeValley = new Valley(leastValuableValley.depth - 1, newNode.prev);
                addValleyToDepth(prevNodeValley.depth, prevNodeValley);
                insertValley(prevNodeValley, prevNodeValley.depth);
            }
            // least valuable valley's depth will ALWAYS "reset" to 1
            if(leastValuableValley.depth != 1) {
                TreeSet<Valley> oldSet = valleysAtDepth.get(leastValuableValley.depth);
                if (oldSet != null) {
                    oldSet.remove(leastValuableValley);
                    // if the set becomes empty after removal, remove the key from the map
                    if (oldSet.isEmpty()) {
                        valleysAtDepth.remove(leastValuableValley.depth);
                    }
                }
                // always the case because it will become the first downward node in a descend point
                leastValuableValley.depth = 1;
                addValleyToDepth(leastValuableValley.depth, leastValuableValley);
            }
        }
        // it's just another point on the descent
        else if(!isValley(newNode) && !isPeak(newNode)) {
            // remove the LVV from the valley map + update it
            TreeSet<Valley> oldSet = valleysAtDepth.get(leastValuableValley.depth);
            if (oldSet != null) {
                oldSet.remove(leastValuableValley);
                // if the set becomes empty after removal, remove the key from the map
                if (oldSet.isEmpty()) {
                    valleysAtDepth.remove(leastValuableValley.depth);
                }
            }
            // increase the depth of the LVV
            leastValuableValley.depth++;
            // add this new node to that set
            addValleyToDepth(leastValuableValley.depth, leastValuableValley);
        }


        return true;
    }

    /**
     * A method to remove the most valuable valley of the specified depth
     *
     * @param depth The depth of the valley we wish to remove
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the removed valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair removeMostValuableValley(int depth) {
        TreeSet<Valley> valleys = valleysAtDepth.get(depth);
        // need to check if that depth even exists
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }
        Valley mostValuableValley = valleys.last(); // returns the most valuable valley @ that depth
        int height = mostValuableValley.node.height;
        int value = mostValuableValley.node.value;
        Node prevNode = mostValuableValley.node.prev;
        Node nextNode = mostValuableValley.node.next;

        // remove the node from the landscape
        removeFromLandscapeDLL(mostValuableValley.node);
        // remove the node from the valley DLL
        removeFromValleyDLL(mostValuableValley);
        // remove the node from the treemap
        valleys.remove(mostValuableValley);
        if(valleys.isEmpty()) {
            valleysAtDepth.remove(mostValuableValley.depth);
        }

        // now check the various conditions:
        // 1. the previous node becomes a valley - BEST CASE
        //      a. decrease the depth by 1
        //      b. insert the prev node into the valley DLL
        // 2. the prev node does NOT become a valley - WORST CASE
        //      a. recompute the valleys starting at the next node
        if(prevNode != null && isValley(prevNode)) {
            depth--;
            Valley newNodeValley = new Valley(depth, prevNode);
            addValleyToDepth(depth, newNodeValley);
            insertValley(newNodeValley, depth);
        } else {
            recomputeValleys(nextNode);
        }
        return new IntPair(height, value);
    }

    /**
     * A method to remove the least valuable valley of the specified depth
     *
     * @param depth The depth of the valley we wish to remove
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the removed valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair removeLeastValuableValley(int depth) {
        TreeSet<Valley> valleys = valleysAtDepth.get(depth);
        // need to check if that depth even exists
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }
        Valley leastValuableValley = valleys.first(); // returns the least valuable valley @ that depth
        int height = leastValuableValley.node.height;
        int value = leastValuableValley.node.value;
        Node prevNode = leastValuableValley.node.prev;
        Node nextNode = leastValuableValley.node.next;

        // remove the node from the landscape
        removeFromLandscapeDLL(leastValuableValley.node);
        // remove the node from the valley DLL
        removeFromValleyDLL(leastValuableValley);
        // remove the node from the treemap
        valleys.remove(leastValuableValley);
        if(valleys.isEmpty()) {
            valleysAtDepth.remove(leastValuableValley.depth);
        }

        // now check the various conditions:
        // 1. the previous node becomes a valley - BEST CASE
        //      a. decrease the depth by 1
        //      b. insert the prev node into the valley DLL
        // 2. the prev node does NOT become a valley - WORST CASE
        //      a. recompute the valleys starting at the next node
        if(prevNode != null && isValley(prevNode)) {
            depth--;
            Valley newNodeValley = new Valley(depth, prevNode);
            addValleyToDepth(depth, newNodeValley);
            insertValley(newNodeValley, depth);
        } else {
            recomputeValleys(nextNode);
        }

        return new IntPair(height, value);
    }

    private void recomputeValleys(Node startingNode) {
        Node curr = startingNode;
        int prevDepth = 0;
        while (curr != null) {
            int depth = 0;

            if(isPeak(curr)) {
                depth = 0;
            } else {
                depth = prevDepth + 1;
            }

            prevDepth = depth;

            if(isValley(curr)) {
                Valley valley = new Valley(depth, curr);
                TreeSet<Valley> oldSet = valleysAtDepth.get(valley.depth - 1);
                if (oldSet != null) {
                    oldSet.remove(valley);
                    // if the set becomes empty after removal, remove the key from the map
                    if (oldSet.isEmpty()) {
                        valleysAtDepth.remove(valley.depth - 1);
                    }
                }
                insertValley(valley, depth);
            }
            curr = curr.next;
        }
    }

    private void removeFromLandscapeDLL(Node node) {
        if (node == head) {
            head = node.next;
        } else if (node == tail) {
            tail = node.prev;
        }
        if(node.prev != null) {
            node.prev.next = node.next;
        }
        if(node.next != null) {
            node.next.prev = node.prev;
        }

        node.prev = null;
        node.next = null;
    }

    private void removeFromValleyDLL(Valley valley) {
        if (valley == valleyHead) {
            valleyHead = valleyHead.valleyNext;
        }
        if (valley == valleyTail) {
            valleyTail = valley.valleyPrev;
        }
        if(valley.valleyPrev != null) {
            valley.valleyPrev.valleyNext = valley.valleyNext;
        }
        if(valley.valleyNext != null) {
            valley.valleyNext.valleyPrev = valley.valleyPrev;
        }

        valley.valleyPrev = null;
        valley.valleyNext = null;
    }

    /**
     * A method to get the treasure value of the most valuable valley of the
     * specified depth
     *
     * @param depth The depth of the valley we wish to find the treasure value of
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the found valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair getMostValuableValley(int depth) {
        TreeSet<Valley> valleys = valleysAtDepth.get(depth);
        // need to check if that depth even exists
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }
        Valley mostValuableValley = valleys.last(); // returns the most valuable valley @ that depth
        return new IntPair(mostValuableValley.node.height, mostValuableValley.node.value);
    }

    /**
     * A method to get the treasure value of the least valuable valley of the
     * specified depth
     *
     * @param depth The depth of the valley we wish to find the treasure value of
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the found valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair getLeastValuableValley(int depth) {
        TreeSet<Valley> valleys = valleysAtDepth.get(depth);
        // need to check if that depth even exists
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }
        Valley leastValuableValley = valleys.first(); // returns the least valuable valley @ that depth
        return new IntPair(leastValuableValley.node.height, leastValuableValley.node.value);
    }

    /**
     * A method to get the number of valleys of a given depth
     *
     * @param depth The depth that we want to count valleys for
     *
     * @return The number of valleys of the specified depth
     */
    public int getValleyCount(int depth) {
        return valleysAtDepth.get(depth).size();
    }
}