import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
        int value;
        Node next, prev;
        
        public Node(int value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private class Valley {
        int height, value, depth;
        
        public Valley(int height, int value, int depth) {
            this.height = height;
            this.value = value;
            this.depth = depth;
        }
    }

    // Create instance variables here.
    private Node landscapeHead, landscapeTail; // linked list for the landscape
    private Node valleyHead, valleyTail; // linked list for the valleys
    int[] heights, values;

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
            Node newNode = new Node(values[i]);
            insertAtLandscapeHead(newNode);
        }
        // initialize the second DLL with the valley tracking
        initializeValleys();
    }

    /**
     * Insert a node at the landscape head.
     * 
     * @param node the node to be inserted
     */
    private void insertAtLandscapeHead(Node node) {
        if(landscapeHead == null) {
            landscapeHead = landscapeTail = node;
        } else {
            node.next = landscapeHead;
            landscapeHead.prev = node;
            landscapeHead = node;
        }
    }

    /**
     * Initialize the valley doubly-linked list.
     */
    private void initializeValleys() {
        Node current = landscapeHead;
        int prevDepth = 0;
        boolean inDescent = false;
        int index = 0;
    
        while (current != null) {
            int depth = 0;
    
            int height = heights[index];
            int prevHeight = Integer.MIN_VALUE;
            if(index > 0) {
                prevHeight = heights[index - 1];
            }
    
            // reset depth at peaks and track depth based on the formula given
            if (isPeak(current)) {
                depth = 0;
                inDescent = true;
            } else if (inDescent && height < prevHeight) {
                depth = prevDepth + 1;
            } else {
                inDescent = false;
            }
    
            prevDepth = depth;
    
            // store the valley
            if (isValley(current)) {
                Valley valley = new Valley(height, values[index], depth);
                insertValley(valley, depth);
            }
    
            current = current.next;
            index++;
        }
    }    

    /**
     * Inserts the valley into the valley linked list.
     * 
     * @param valley the valley to insert
     * @param depth the depth to insert at
     */
    private void insertValley(Valley valley, int depth) {
        // Insert into the valley linked list
        Node newNode = new Node(valley.height);
        if (valleyHead == null) {
            valleyHead = valleyTail = newNode;
        } else {
            valleyTail.next = newNode;
            newNode.prev = valleyTail;
            valleyTail = newNode;
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
    private boolean isValley(Node node) {
        if (node.prev == null && node.next != null) {
            return node.value < node.next.value;
        }
        
        if (node.next == null && node.prev != null) {
            return node.value < node.prev.value;
        }
        
        if (node.prev != null && node.next != null) {
            return node.value < node.prev.value && node.value < node.next.value;
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
            return node.value > node.next.value;
        }
        
        if (node.next == null && node.prev != null) {
            return node.value > node.prev.value;
        }
        
        if (node.prev != null && node.next != null) {
            return node.value > node.prev.value && node.value > node.next.value;
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
        return landscapeHead == null;
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
        if (!valleysAtDepth.containsKey(depth)) {
            return false;
        }
    
        TreeSet<Valley> valleysAtThisDepth = valleysAtDepth.get(depth);
        if (valleysAtThisDepth.isEmpty()) {
            return false;
        }
    
        Valley mostValuableValley = valleysAtThisDepth.last();
    
        Node prevNode = landscapeHead;
        Node nextNode = landscapeHead;
    
        while (nextNode.next != null && nextNode.value != mostValuableValley.height) {
            prevNode = nextNode;
            nextNode = nextNode.next;
        }

        Node newNode = new Node(height);

        if (nextNode.next != null) {
            newNode.next = nextNode.next;
            nextNode.next.prev = newNode;
        }

        nextNode.next = newNode;
        newNode.prev = nextNode;

        Valley newValley = new Valley(height, value, depth);
        addValleyToDepth(depth, newValley);

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
        // TODO: Implement the insertAtLeastValuableValley method
        return false;
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
        if (!valleysAtDepth.containsKey(depth) || valleysAtDepth.get(depth).isEmpty()) {
            return null;
        }

        TreeSet<Valley> valleysAtThisDepth = valleysAtDepth.get(depth);
        Valley mostValuableValley = valleysAtThisDepth.pollLast();
        // remove from the valley list
        if (valleysAtThisDepth.isEmpty()) {
            valleysAtDepth.remove(depth);
        }

        // remove from the landscape linked list
        Node currentNode = landscapeHead;
        while (currentNode != null && currentNode.value != mostValuableValley.height) {
            currentNode = currentNode.next;
        }

        if (currentNode != null) {
            Node prevNode = currentNode.prev;
            Node nextNode = currentNode.next;

            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                landscapeHead = nextNode;
            }

            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                landscapeTail = prevNode;
            }

            return new IntPair(mostValuableValley.height, mostValuableValley.value);
        }

        return null;
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
        // TODO: Implement the removeLeastValuableValley method
        return null;
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

        for(Valley v : valleysAtThisDepth) {
            System.out.println("valley value: " + v.value + " valley depth: " + v.depth);
        }
        
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
}