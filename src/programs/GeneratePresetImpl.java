package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;
import com.battle.heroes.army.programs.computer.ComputerArcherProgram;
import com.battle.heroes.army.programs.user.UserArcherProgram;

import java.util.*;

/**
 * Отвечает за генерацию пресета армии противника.
 */
public class GeneratePresetImpl implements GeneratePreset {

    private static final int computerArmyWidth = 3;
    private static final int computerArmyHeight = 21;
    private static final int unitMaxQuantity = 11;

    // Сперва атака/стоимость, далее здоровье/стоимость, не более 11 юнитов каждого типа
    public Army generate(List<Unit> unitList, int maxPoints) {

        // Координаты юнитов
        List<Coordinates> coordinates = new ArrayList<>();

        // Счётчик юнитов по типу
        Map<Unit, Integer> counts = new HashMap<>();
        for (Unit unit : unitList) {
            counts.put(unit, 0);
        }

        List<Unit> armyUnits = new ArrayList<>();
        int totalCost = 0;

        // Максимум юнитов в армии — не более unitMaxQuantity * N + ограничение бюджетом
        // Добавляем, пока есть возможность
        boolean canAdd;
        do {
            canAdd = false;
            Unit best = null;

            for (Unit candidate : unitList) {
                if (counts.get(candidate) >= unitMaxQuantity) continue;
                if (totalCost + candidate.getCost() > maxPoints) continue;

                if (best == null || compareUnits(candidate, best)) {
                    best = candidate;
                }
            }

            if (best != null) {
                int unitCounter = counts.get(best);
                armyUnits.add(copyUnit(best, unitCounter + 1, coordinates));
                counts.merge(best, 1, Integer::sum);
                totalCost += best.getCost();
                canAdd = true;
            }
        } while (canAdd);

        Army army = new Army();
        army.setUnits(armyUnits);
        army.setPoints(totalCost);
        return army;
    }


    // Возвращает true, если u1 эффективнее u2
    private boolean compareUnits(Unit u1, Unit u2) {

        // Сначала сравниваем по соотношению АТК/СТОИМОСТЬ
        double r1Attack = (double) u1.getBaseAttack() / u1.getCost();
        double r2Attack = (double) u2.getBaseAttack() / u2.getCost();

        if (Math.abs(r1Attack - r2Attack) > 1e-9) {
            return r1Attack > r2Attack;
        }

        // Далее сравниваем по соотношению ОЗ/СТОИМОСТЬ
        double r1Health = (double) u1.getHealth() / u1.getCost();
        double r2Health = (double) u2.getHealth() / u2.getCost();
        return r1Health > r2Health;
    }


    private Unit copyUnit(Unit original, int unitCounter, List<Coordinates> coordinatesList) {
        Random random = new Random();

        Coordinates coordinates1 = new Coordinates(random.nextInt(computerArmyWidth),
                random.nextInt(computerArmyHeight));

        // Генерируем координаты для нового юнита в допустимом диапазоне, если в текущих координатах уже стоит юнит
        while(coordinatesList.contains(coordinates1)){
            coordinates1.setX(random.nextInt(computerArmyWidth));
            coordinates1.setY(random.nextInt(computerArmyHeight));
        }
        coordinatesList.add(coordinates1);

        return new Unit(original.getName() + " " + unitCounter,
                original.getUnitType(),
                original.getHealth(),
                original.getBaseAttack(),
                original.getCost(),
                original.getAttackType(),
                original.getAttackBonuses(),
                original.getDefenceBonuses(),
                coordinates1.getX(),
                coordinates1.getY());
    }

    // Класс для описания координат юнита
    public static class Coordinates{
        private int x;
        private int y;

        public Coordinates(){}
        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            Coordinates that = (Coordinates) object;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}