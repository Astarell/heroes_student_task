package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Отвечает за создание перечня подходящих для атаки юнитов.
 */
public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    // Итерируемся по рядам и ищем для армии компьютера крайнего правого (не прикрытого справа)
    // А для армии игрока ищем крайнего левого (не прикрытого слева)
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> currentArmySuitableUnits = new ArrayList<>();

        for (List<Unit> row : unitsByRow){
            Unit suitableUnit = null;

            for (Unit unit : row){
                // Нужны только живые
                if (!unit.isAlive()) {
                    continue;
                }

                // Если атакуют армию компьютера, то координаты юнита [computerX, computerY]
                if (isLeftArmyTarget){
                    if (suitableUnit == null || unit.getxCoordinate() > suitableUnit.getxCoordinate()){
                        suitableUnit = unit;
                    }
                }
                // Атакуют армию игрока
                else{
                    if (suitableUnit == null || unit.getxCoordinate() < suitableUnit.getxCoordinate()){
                        suitableUnit = unit;
                    }
                }
            }

            if (suitableUnit != null)
                currentArmySuitableUnits.add(suitableUnit);
        }

        return currentArmySuitableUnits;
    }
}
