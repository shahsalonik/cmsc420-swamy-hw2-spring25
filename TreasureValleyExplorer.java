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

    /**
     * A Node class for each of the elements of the landscape.
     * 
     * Stores their heights and values.
     */
    private class Node {
        int height, value;

        public Node(int height, int value) {
            this.height = height;
            this.value = value;
        }
    }

    /**
     * A Valley class to store data related to the valleys.
     * 
     * Stores their heights and values along with their depths and what indices they are located at.
     */
    private class Valley {
        int height, value, depth, index;

        public Valley(int height, int value, int depth, int index) {
            this.height = height;
            this.value = value;
            this.depth = depth;
            this.index = index;
        }
    }

    private List<Node> landscape;
    // different maps to store the most and least valuable valleys
    private Map<Integer, TreeMap<Integer, Valley>> mostValuableValleyMap;
    private Map<Integer, TreeMap<Integer, Valley>> leastValuableValleyMap;

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
        landscape = new ArrayList<>();
        mostValuableValleyMap = new HashMap<>();
        leastValuableValleyMap = new HashMap<>();
        // initialize landscape
        for (int i = 0; i < heights.length; i++) {
            landscape.add(new Node(heights[i], values[i]));
        }
        // go through and compute the valleys
        initializeValleys();

    }

    private void initializeValleys() {
        // clear out the valley maps so that we can start fresh
        mostValuableValleyMap.clear();
        leastValuableValleyMap.clear();

        // compute the depths of each node using the depth formula we got
        int depth = 0;
        for (int i = 0; i < landscape.size(); i++) {
            Node curr = landscape.get(i);
            // increase depth if we're on a descend sequence, otherwise reset to 0
            if (i > 0 && curr.height < landscape.get(i - 1).height) {
                depth++;
            } else {
                depth = 0;
            }

            // valley check + add to valley maps
            if (isValley(i, curr)) {
                Valley valley = new Valley(curr.height, curr.value, depth, i);
                mostValuableValleyMap.computeIfAbsent(depth, v -> new TreeMap<>()).put(valley.value, valley);
                leastValuableValleyMap.computeIfAbsent(depth, v -> new TreeMap<>()).put(valley.value, valley);
            }
        }

    }

    /**
     * Checks whether the given node is a valley using the conditions provided.
     *
     * @param node the node to check conditions for
     * @return true if it is a valley, false if not
     */
    private boolean isValley(int index, Node node) {
        return (index == 0 || node.height < landscape.get(index - 1).height) && (index == landscape.size() - 1 || node.height < landscape.get(index + 1).height);
    }

    /**
     * Checks if the entire landscape is excavated (i.e., there are no points
     * left).
     *
     * @return true if the landscape is empty, false otherwise.
     */
    public boolean isEmpty() {
        return landscape.isEmpty();
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
        TreeMap<Integer, Valley> currDepthMap = mostValuableValleyMap.get(depth);
        // if no valleys at specified depth, return null
        if (currDepthMap == null || currDepthMap.isEmpty()) {
            return false;
        }

        // find where the most valuable valley is and insert at the index before that one
        Valley mostValuableValley = currDepthMap.lastEntry().getValue();
        int index = mostValuableValley.index;
        Node newNode = new Node(height, value);
        landscape.add(index, newNode);
        initializeValleys();
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
        TreeMap<Integer, Valley> currDepthMap = leastValuableValleyMap.get(depth);
        // if no valleys at specified depth, return null
        if (currDepthMap == null || currDepthMap.isEmpty()) {
            return false;
        }

        // find where the least valuable valley is and insert at the index before that one
        Valley leastValuableValley = currDepthMap.firstEntry().getValue();
        int index = leastValuableValley.index;
        Node newNode = new Node(height, value);
        landscape.add(index, newNode);
        initializeValleys();
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
        TreeMap<Integer, Valley> currDepthMap = mostValuableValleyMap.get(depth);
        // if no valleys at specified depth, return null
        if (currDepthMap == null || currDepthMap.isEmpty()) {
            return null;
        }

        // otherwise, remove the valley from the map and the landscape
        Valley mostValuableValley = currDepthMap.pollLastEntry().getValue();
        int indexToRemove = mostValuableValley.index;

        Node removed = landscape.remove(indexToRemove);
        // need to reinitialize the valleys (is this too much computational time??)
        // come back to this if it doesn't pass the tests lol
        initializeValleys();
        return new IntPair(removed.height, removed.value);
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
        TreeMap<Integer, Valley> currDepthMap = leastValuableValleyMap.get(depth);
        // if no valleys at specified depth, return null
        if (currDepthMap == null || currDepthMap.isEmpty()) {
            return null;
        }

        // otherwise, remove the valley from the map and the landscape
        Valley leastValuableValley = currDepthMap.pollFirstEntry().getValue();
        int indexToRemove = leastValuableValley.index;

        Node removed = landscape.remove(indexToRemove);
        // need to reinitialize the valleys (is this too much computational time??)
        // come back to this if it doesn't pass the tests lol
        initializeValleys();
        return new IntPair(removed.height, removed.value);
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
        TreeMap<Integer, Valley> currDepthMap = mostValuableValleyMap.get(depth);
        if (currDepthMap == null) {
            return null;
        }
        // get the valley with the greatest value at that depth
        Valley mostValuableValley = currDepthMap.lastEntry().getValue();
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
        TreeMap<Integer, Valley> currDepthMap = leastValuableValleyMap.get(depth);
        if (currDepthMap == null) {
            return null;
        }
        // get the valley with the lowest value at that depth
        Valley leastValuableValley = currDepthMap.firstEntry().getValue();
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
        TreeMap<Integer, Valley> map = mostValuableValleyMap.get(depth);
        if(map == null) {
            return 0;
        }
        return map.size();
    }
}