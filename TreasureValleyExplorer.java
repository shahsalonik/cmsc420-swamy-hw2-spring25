import java.util.*;

import javax.sound.midi.SysexMessage;

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

    private class Valley {
        int height, depth, value;
        Valley prev, next;
        Valley landscapePrev, landscapeNext;

        public Valley(int height, int value, int depth) {
            this.height = height;
            this.depth = depth;
            this.value = value;
        }
    }

    private Valley landscapeHead, landscapeTail; // linked list for the landscape
    private Valley valleyHead, valleyTail; // linked list for the valleys
    private int[] heights, values;

    // sorted data structure to hold valleys of the same depth
    private Map<Integer, TreeSet<Valley>> valleysAtDepth;

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
        this.landscapeHead = this.landscapeTail = null;
        this.valleyHead = this.valleyTail = null;
        this.valleysAtDepth = new TreeMap<>();
        // initialize new landscape (DLL)
        for(int i = values.length - 1; i >= 0; i--) {
            Valley newNode = new Valley(heights[i], values[i], 0);
            insertAtLandscapeHead(newNode);
        }
        // initialize the second DLL with the valley tracking
        initializeValleys();

        // DEBUGGING
        System.out.println("initialization: ");
        printLandscape();
        System.out.println();
    }

    /**
     * Insert a node at the landscape head.
     *
     * @param node the node to be inserted
     */
    private void insertAtLandscapeHead(Valley node) {
        if(landscapeHead == null) {
            landscapeHead = landscapeTail = node;
        } else {
            node.landscapeNext = landscapeHead;
            landscapeHead.landscapePrev = node;
            landscapeHead = node;
        }
    }


    /**
     * Initialize the valley doubly-linked list.
     */
    private void initializeValleys() {
        Valley current = landscapeHead;
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
            current.depth = depth;

            // store the valley
            if (isValley(current)) {
                Valley valley = new Valley(height, values[index], depth);
                insertValley(valley, depth);
                valley.landscapePrev = current.landscapePrev;
                valley.landscapeNext = current.landscapeNext;
            }

            current = current.landscapeNext;
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
            valley.next = valleyTail.next;
            valley.prev = valleyTail;
            valleyTail.next = valley;
            valleyTail = valley;
        }

        // Insert into the sorted set for its depth
        addValleyToDepth(depth, valley);
    }

    /**
     * Checks whether the given node is a valley using the conditions provided.
     *
     * @param node the node to check conditions for
     * @return true if it is a valley, false if not
     */
    private boolean isValley(Valley node) {
        if (node.landscapePrev == null && node.landscapeNext != null) {
            return node.height < node.landscapeNext.height;
        }

        if (node.landscapeNext == null && node.landscapePrev != null) {
            return node.height < node.landscapePrev.height;
        }

        if (node.landscapePrev != null && node.landscapeNext != null) {
            return node.height < node.landscapePrev.height && node.height < node.landscapeNext.height;
        }

        return true;
    }

    /**
     * Checks whether the given node is a peak using the conditions provided.
     *
     * @param node the node to check conditions for
     * @return true if it is a peak, false if not
     */
    private boolean isPeak(Valley node) {
        if (node.landscapePrev == null && node.landscapeNext != null) {
            return node.height > node.landscapeNext.height;
        }

        if (node.landscapeNext == null && node.landscapePrev != null) {
            return node.height > node.landscapePrev.height;
        }

        if (node.landscapePrev != null && node.landscapeNext != null) {
            return node.height > node.landscapePrev.height && node.height > node.landscapeNext.height;
        }

        return true;
    }

    /**
     * Adds a valley to the sorted set for its particular depth.
     * @param depth the depth to insert the valley at
     * @param valley the valley to insert
     */
    private void addValleyToDepth(int depth, Valley valley) {
        valleysAtDepth.putIfAbsent(depth, new TreeSet<>(Comparator.comparingInt(v -> v.value)));
        valleysAtDepth.get(depth).add(valley);
    }

    /**
     * Checks if the entire landscape is excavated (i.e., there are no points
     * left).
     *
     * @return true if the landscape is empty, false otherwise.
     */
    public boolean isEmpty() {
        return landscapeHead == null && landscapeTail == null;
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
        if(valleysAtDepth.get(depth).isEmpty()) {
            return false;
        }

        Valley mostValuableValley = valleysAtDepth.get(depth).last();
        // the node could be at that depth, but it may not be a valley point
        if(!isValley(mostValuableValley)) {
            return false;
        }
        // do the insertion
        Valley newNode = new Valley(height, value, depth);
        newNode.next = mostValuableValley;
        newNode.prev = mostValuableValley.prev;

        if(mostValuableValley.prev != null) {
            mostValuableValley.prev.next = newNode;
        } else {
            insertAtLandscapeHead(newNode);
        }

        mostValuableValley.prev = newNode;

        // check if this new node is a valley. if yes, increase depth (prev depth + 1) and add to valleysAtDepth
        if(isValley(newNode)) {
            if(newNode.prev != null) {
                newNode.depth = newNode.prev.depth + 1;
                addValleyToDepth(newNode.depth, newNode);
            }
        }

        // 2 cases for insertion:
        // 1. the node you insert has a height < the MVV
            // if this case, then decrease the depth of the following nodes until we reach a peak
            // do this by constantly checking if the following is a peak
        // 2. the node you insert has a height > the MVV
            // if this case, then increase the depth of the MVV until we reach a peak
        if(height < mostValuableValley.height) {
            while()
        } else {

        }


        return false;
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
        // TODO: Implement the insertAtLeastValuableValley method
        return false;
    }

    /**
     * Removes the valley from both the valley doubly linked list and the sorted set for its depth.
     */
    private void removeValley(Valley valley) {
        if(valley == null) {
            return;
        }

        if(valley == valleyHead) {
            valleyHead = valley.next;
        }

        if(valley == valleyTail) {
            valleyTail = valley.prev;
        }

        if(valley.prev != null) {
            valley.prev.next = valley.next;
        }

        if(valley.next != null) {
            valley.next.prev = valley.prev;
        }

        // Remove from sorted set
        if (valleysAtDepth.containsKey(valley.depth)) {
            valleysAtDepth.get(valley.depth).remove(valley);
        }

        // Disconnect the valley node completely
        valley.next = valley.prev = null;
    }

    private void deleteLandscapeNode(Valley node) {
        if(node == null) {
            return;
        }

        if(node == landscapeHead) {
            landscapeHead = node.landscapeNext;
        }

        if(node == landscapeTail) {
            landscapeTail = node.landscapePrev;
        }

        if(node.landscapePrev != null) {
            node.landscapePrev.landscapeNext = node.landscapeNext;
        }

        if(node.landscapeNext != null) {
            node.landscapeNext.landscapePrev = node.landscapePrev;
        }

        node.landscapeNext = node.landscapePrev = null;
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
        // first check if the depth even exists
        if(valleysAtDepth.get(depth) == null) {
            return null;
        }

        Valley mostValuableValley = valleysAtDepth.get(depth).last();
        // the node could be at that depth, but it may not be a valley point
        if(!isValley(mostValuableValley)) {
            return null;
        }
        Valley nextNode = mostValuableValley.landscapeNext;

        // does the removal here
        deleteLandscapeNode(mostValuableValley);
        removeValley(mostValuableValley);

        System.out.println("after removal: ");
        printLandscape();

        // then we want to check the depths of the nodes following it
        // 2 cases:
        // 1. the node following it is now a peak (in which case depths don't need to be updated)
        // 2. the node following it is NOT a peak and now the depths of the following until the next peak need to be decreased by 1
        if(nextNode != null && isPeak(nextNode)) {
            return new IntPair(mostValuableValley.height, mostValuableValley.value);
        } else if(nextNode != null) {
            while(nextNode != null && !isPeak(nextNode)) {
                nextNode.depth++;
                if(isValley(nextNode)) {
                    TreeSet<Valley> oldSet = valleysAtDepth.get(nextNode.depth - 1);
                    if (oldSet != null) {
                        oldSet.remove(nextNode);
                        // if the set becomes empty after removal, remove the key from the map
                        if (oldSet.isEmpty()) {
                            valleysAtDepth.remove(nextNode.depth - 1);
                        }
                    }
                    addValleyToDepth(nextNode.depth, nextNode);
                }
                nextNode = nextNode.landscapeNext;
            }
        }

        System.out.println("after relinking/updating: ");
        printLandscape();

        return new IntPair(mostValuableValley.height, mostValuableValley.value);

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
        // first check if the depth even exists
        if(valleysAtDepth.get(depth) == null) {
            return null;
        }

        Valley leastValuableValley = valleysAtDepth.get(depth).first();
        // the node could be at that depth, but it may not be a valley point
        if(!isValley(leastValuableValley)) {
            return null;
        }
        Valley nextNode = leastValuableValley.landscapeNext;

        // does the removal here
        deleteLandscapeNode(leastValuableValley);
        removeValley(leastValuableValley);

        System.out.println("after removal: ");
        printLandscape();

        // then we want to check the depths of the nodes following it
        // 2 cases:
        // 1. the node following it is now a peak (in which case depths don't need to be updated)
        // 2. the node following it is NOT a peak and now the depths of the following until the next peak need to be decreased by 1
        if(nextNode != null && isPeak(nextNode)) {
            return new IntPair(leastValuableValley.height, leastValuableValley.value);
        } else if(nextNode != null) {
            while(nextNode != null && !isPeak(nextNode)) {
                nextNode.depth++;
                if(isValley(nextNode)) {
                    TreeSet<Valley> oldSet = valleysAtDepth.get(nextNode.depth - 1);
                    if (oldSet != null) {
                        oldSet.remove(nextNode);
                        // if the set becomes empty after removal, remove the key from the map
                        if (oldSet.isEmpty()) {
                            valleysAtDepth.remove(nextNode.depth - 1);
                        }
                    }
                    addValleyToDepth(nextNode.depth, nextNode);
                }
                nextNode = nextNode.landscapeNext;
            }
        }

        System.out.println("after relinking/updating: ");
        printLandscape();

        return new IntPair(leastValuableValley.height, leastValuableValley.value);
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
        if (!valleysAtDepth.containsKey(depth)) {
            return null;
        }

        TreeSet<Valley> valleysAtThisDepth = valleysAtDepth.get(depth);

        if (valleysAtThisDepth.isEmpty()) {
            return null;
        }

        Valley mostValuableValley = valleysAtThisDepth.last();

        return new IntPair(mostValuableValley.height, mostValuableValley.value);
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
        if (!valleysAtDepth.containsKey(depth)) {
            return null;
        }

        TreeSet<Valley> valleysAtThisDepth = valleysAtDepth.get(depth);

        if (valleysAtThisDepth.isEmpty()) {
            return null;
        }

        Valley leastValuableValley = valleysAtThisDepth.first();

        return new IntPair(leastValuableValley.height, leastValuableValley.value);
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

    private void printLandscape() {
        for(Valley curr = landscapeHead; curr != null; curr = curr.landscapeNext) {
            System.out.println("node height, value: " + curr.height + ", " + curr.value);
        }

        System.out.println("-----");

        for(Valley curr = valleyHead; curr != null; curr = curr.next) {
            System.out.println("valley height, value: " + curr.height + ", " + curr.value);
        }

        System.out.println("-----");

        for(Integer i : valleysAtDepth.keySet()) {
            for(Valley v : valleysAtDepth.get(i)) {
                System.out.println("valley depth, height, value: " + v.depth + ", " + v.height + ", " + v.value);
            }
        }
    }
}