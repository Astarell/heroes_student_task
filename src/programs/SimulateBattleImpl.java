package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Отвечает за осуществление симуляции боя.
 */
public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {

        // Пока в обеих армиях есть живые юниты - будет битва
        while (playerArmy.getUnits().stream().anyMatch(Unit::isAlive)
                && computerArmy.getUnits().stream().anyMatch(Unit::isAlive)){

            // Берем всех живых юнитов обеих армий
            List<Unit> allAliveUnits = new ArrayList<>();
            allAliveUnits.addAll(playerArmy.getUnits().stream().filter(Unit::isAlive).toList());
            allAliveUnits.addAll(computerArmy.getUnits().stream().filter(Unit::isAlive).toList());

            // Сортируем по убыванию АТК
            allAliveUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            for (Unit attacker : allAliveUnits){
                // Если юнит мертв - пропускаем
                if (!attacker.isAlive()){
                    continue;
                }

                Unit targetUnit = attacker.getProgram().attack();
                printBattleLog.printBattleLog(attacker, targetUnit);
            }
        }
    }
}