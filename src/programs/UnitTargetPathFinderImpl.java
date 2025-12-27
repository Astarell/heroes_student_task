package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

/**
 * Отвечает за поиск наикратчайшего пути между атакующим и атакуемым юнитом.
 */
public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    // 8 направлений: горизонталь, вертикаль, диагонали
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int endX = targetUnit.getxCoordinate();
        int endY = targetUnit.getyCoordinate();

        // Создаем множество занятых клеток, НО ИСКЛЮЧАЕМ атакующего и цель
        Set<String> obstacles = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit == attackUnit || unit == targetUnit) {
                continue; // они не мешают самим себе
            }
            obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
        }

        // A* алгоритм
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<String> closedSet = new HashSet<>();
        Map<String, String> cameFrom = new HashMap<>();

        Node startNode = new Node(startX, startY, 0, heuristic(startX, startY, endX, endY));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            String currentKey = current.x + "," + current.y;

            if (current.x == endX && current.y == endY) {
                return reconstructPath(cameFrom, currentKey);
            }

            closedSet.add(currentKey);

            for (int[] dir : DIRECTIONS) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) {
                    continue;
                }

                String neighborKey = nx + "," + ny;

                if (obstacles.contains(neighborKey) || closedSet.contains(neighborKey)) {
                    continue;
                }

                int cost = 1;
                int tentativeG = current.g + cost;

                // Проверяем, есть ли сосед в openSet
                Node existing = null;
                for (Node node : openSet) {
                    if (node.x == nx && node.y == ny) {
                        existing = node;
                        break;
                    }
                }

                if (existing == null) {
                    Node newNode = new Node(nx, ny, tentativeG, heuristic(nx, ny, endX, endY));
                    openSet.add(newNode);
                    cameFrom.put(neighborKey, currentKey);
                } else if (tentativeG < existing.g) {
                    // Удаляем старый и добавляем новый (т.к. PriorityQueue не поддерживает update)
                    openSet.remove(existing);
                    Node newNode = new Node(nx, ny, tentativeG, heuristic(nx, ny, endX, endY));
                    openSet.add(newNode);
                    cameFrom.put(neighborKey, currentKey);
                }
            }
        }

        return new ArrayList<>(); // путь не найден
    }

    private int heuristic(int x, int y, int targetX, int targetY) {
        return Math.max(Math.abs(x - targetX), Math.abs(y - targetY));
    }

    // Восстановление пути
    private List<Edge> reconstructPath(Map<String, String> cameFrom, String currentKey) {
        List<Edge> path = new ArrayList<>();
        String key = currentKey;

        while (key != null) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            path.add(new Edge(x, y));

            key = cameFrom.get(key);
        }

        Collections.reverse(path);
        return path;
    }

    // Внутренний класс для узла
    private static class Node {
        int x, y;
        int g; // стоимость пути от начала
        int h; // эвристика до цели
        int f; // g + h

        Node(int x, int y, int g, int h) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }
}
