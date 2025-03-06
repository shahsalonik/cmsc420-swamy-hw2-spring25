import java.util.*;

/**
 * A convenient class that stores a pair of integers.
 * DO NOT MODIFY THIS CLASS.
 */
class IntPair2 {
    public final int first;
    public final int second;

    public IntPair2(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IntPair2 other = (IntPair2) obj;
        return first == other.first && second == other.second;
    }

    @Override
    public int hashCode() {
        return 31 * first + second;
    }
}

/**
 * Represents a valley in the landscape.
 */
class Valley {
    int height;
    int value;
    int depth;
    Valley prev, next;

    public Valley(int height, int value, int depth) {
        this.height = height;
        this.value = value;
        this.depth = depth;
    }
}

/**
 * Represents a landform in the landscape.
 */
class Node {
    int height;
    int value;
    Node prev, next;

    public Node(int height, int value) {
        this.height = height;
        this.value = value;
    }
}

/**
 * TreasureValleyExplorer class operates on a landscape of Numerica,
 * selectively modifying the most and least valuable valleys of a specified depth.
 */
public class TreasureValleyExplorer_maybe {
    private Node head;
    private TreeMap<Integer, TreeSet<Valley>> valleyMap;

    public TreasureValleyExplorer_maybe(int[] heights, int[] values) {
        valleyMap = new TreeMap<>();
        Node prev = null;
        for (int i = 0; i < heights.length; i++) {
            Node node = new Node(heights[i], values[i]);
            if (prev != null) {
                prev.next = node;
                node.prev = prev;
            } else {
                head = node;
            }
            prev = node;
        }
        computeValleys();
    }

    private void computeValleys() {
        for (Node curr = head; curr != null; curr = curr.next) {
            if (isValley(curr)) {
                int depth = computeDepth(curr);
                Valley valley = new Valley(curr.height, curr.value, depth);
                valleyMap.computeIfAbsent(depth, k -> new TreeSet<>(Comparator.comparingInt(v -> v.value))).add(valley);
            }
        }
    }

    private boolean isValley(Node node) {
        return node.prev != null && node.next != null && node.height < node.prev.height && node.height < node.next.height;
    }

    private int computeDepth(Node node) {
        int depth = 0;
        Node curr = node;
        while (curr.prev != null && curr.prev.height > curr.height) {
            depth++;
            curr = curr.prev;
        }
        return depth;
    }

    private void updateDepths() {
        valleyMap.clear();
        computeValleys();
    }

    public boolean isEmpty() {
        return head == null;
    }

    public boolean insertAtMostValuableValley(int height, int value, int depth) {
        TreeSet<Valley> valleys = valleyMap.get(depth);
        if (valleys == null || valleys.isEmpty()) return false;
        Valley mostValuable = valleys.last();
        insertBefore(mostValuable, height, value);
        updateDepths();
        return true;
    }

    public boolean insertAtLeastValuableValley(int height, int value, int depth) {
        TreeSet<Valley> valleys = valleyMap.get(depth);
        if (valleys == null || valleys.isEmpty()) return false;
        Valley leastValuable = valleys.first();
        insertBefore(leastValuable, height, value);
        updateDepths();
        return true;
    }

    private void insertBefore(Valley valley, int height, int value) {
        Node newNode = new Node(height, value);
        Node curr = head;
        while (curr != null && curr.height != valley.height) {
            curr = curr.next;
        }
        if (curr == null) return;
        newNode.next = curr;
        newNode.prev = curr.prev;
        if (curr.prev != null) curr.prev.next = newNode;
        curr.prev = newNode;
    }

    public IntPair removeMostValuableValley(int depth) {
        TreeSet<Valley> valleys = valleyMap.get(depth);
        if (valleys == null || valleys.isEmpty()) return null;
        Valley mostValuable = valleys.pollLast();
        removeNode(mostValuable);
        updateDepths();
        return new IntPair(mostValuable.height, mostValuable.value);
    }

    public IntPair removeLeastValuableValley(int depth) {
        TreeSet<Valley> valleys = valleyMap.get(depth);
        if (valleys == null || valleys.isEmpty()) return null;
        Valley leastValuable = valleys.pollFirst();
        removeNode(leastValuable);
        updateDepths();
        return new IntPair(leastValuable.height, leastValuable.value);
    }

    private void removeNode(Valley valley) {
        Node curr = head;
        while (curr != null && curr.height != valley.height) {
            curr = curr.next;
        }
        if (curr == null) return;
        if (curr.prev != null) curr.prev.next = curr.next;
        if (curr.next != null) curr.next.prev = curr.prev;
        if (curr == head) head = curr.next;
    }

    public IntPair getMostValuableValley(int depth) {
        TreeSet<Valley> valleys = valleyMap.get(depth);
        if (valleys == null || valleys.isEmpty()) return null;
        Valley mostValuable = valleys.last();
        return new IntPair(mostValuable.height, mostValuable.value);
    }

    public IntPair getLeastValuableValley(int depth) {
        TreeSet<Valley> valleys = valleyMap.get(depth);
        if (valleys == null || valleys.isEmpty()) return null;
        Valley leastValuable = valleys.first();
        return new IntPair(leastValuable.height, leastValuable.value);
    }

    public int getValleyCount(int depth) {
        return valleyMap.getOrDefault(depth, new TreeSet<>()).size();
    }
}