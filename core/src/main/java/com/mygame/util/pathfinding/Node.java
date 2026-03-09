package com.mygame.util.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    public final int x;
    public final int y;
    public boolean isWalkable;

    public float gCost;
    public float hCost;
    public Node parent;

    public List<Node> neighbors = new ArrayList<>();

    public Node(int x, int y, boolean isWalkable) {
        this.x = x;
        this.y = y;
        this.isWalkable = isWalkable;
    }

    public float getFCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
