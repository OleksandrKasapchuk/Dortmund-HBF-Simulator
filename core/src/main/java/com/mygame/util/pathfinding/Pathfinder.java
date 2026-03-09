package com.mygame.util.pathfinding;

import java.util.*;

public class Pathfinder {
    private final Node[][] nodes;
    private final int width;
    private final int height;

    public Pathfinder(boolean[][] collisionGrid) {
        this.width = collisionGrid.length;
        this.height = collisionGrid[0].length;
        this.nodes = new Node[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new Node(x, y, !collisionGrid[x][y]);
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                addNeighbors(nodes[x][y]);
            }
        }
    }

    private void addNeighbors(Node node) {
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : dirs) {
            int nx = node.x + dir[0];
            int ny = node.y + dir[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                node.neighbors.add(nodes[nx][ny]);
            }
        }
    }

    public void setNodeWalkable(int x, int y, boolean walkable) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            nodes[x][y].isWalkable = walkable;
        }
    }

    public List<Node> findPath(int startX, int startY, int targetX, int targetY) {
        startX = clamp(startX, 0, width - 1);
        startY = clamp(startY, 0, height - 1);
        targetX = clamp(targetX, 0, width - 1);
        targetY = clamp(targetY, 0, height - 1);

        Node startNode = nodes[startX][startY];
        Node targetNode = nodes[targetX][targetY];

        // Robustness: If start or target is not walkable, find nearest walkable
        if (!startNode.isWalkable) {
            startNode = findNearestWalkableNode(startX, startY);
            if (startNode == null) return Collections.emptyList();
        }
        if (!targetNode.isWalkable) {
            targetNode = findNearestWalkableNode(targetX, targetY);
            if (targetNode == null) return Collections.emptyList();
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));
        Set<Node> closedSet = new HashSet<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y].gCost = Float.MAX_VALUE;
                nodes[x][y].parent = null;
            }
        }

        startNode.gCost = 0;
        startNode.hCost = getDistance(startNode, targetNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(targetNode)) {
                return retracePath(startNode, targetNode);
            }

            closedSet.add(current);

            for (Node neighbor : current.neighbors) {
                if (!neighbor.isWalkable || closedSet.contains(neighbor)) continue;

                float movementCost = current.gCost + getDistance(current, neighbor);
                if (movementCost < neighbor.gCost) {
                    neighbor.gCost = movementCost;
                    neighbor.hCost = getDistance(neighbor, targetNode);
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private Node findNearestWalkableNode(int x, int y) {
        // Search in expanding squares
        for (int r = 1; r < 5; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) != r && Math.abs(dy) != r) continue;
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        if (nodes[nx][ny].isWalkable) return nodes[nx][ny];
                    }
                }
            }
        }
        return null;
    }

    private List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;
        while (current != null && !current.equals(startNode)) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private float getDistance(Node a, Node b) {
        int dstX = Math.abs(a.x - b.x);
        int dstY = Math.abs(a.y - b.y);
        if (dstX > dstY) return 1.414f * dstY + (dstX - dstY);
        return 1.414f * dstX + (dstY - dstX);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}
