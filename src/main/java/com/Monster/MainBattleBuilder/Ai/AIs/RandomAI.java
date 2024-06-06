package com.Monster.MainBattleBuilder.Ai.AIs;

import org.MonsterBattler.Effect;
import org.MonsterBattler.Monster;
import org.MonsterBattler.MoveEffect;
import org.MonsterBattler.TurnInfoPackage;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RandomAI {
    /*
    To select move we need:
    TIP
     */
    Random rand = new Random();

    public MoveEffect[] getRandomMoveChoice(TurnInfoPackage turnInfoPackage){
        //Get The activeMons
        int activeMons = turnInfoPackage.getActiveMon();
        int monInTeam = turnInfoPackage.getMonInTeam();

        // Build the array of Ai monsters
        Monster[] monsters = new Monster[activeMons];
        for (int i = 0; i < activeMons; i++) {
            monsters[i] = turnInfoPackage.getMonsterBySlot(turnInfoPackage.convertIntToSlot(monInTeam+i));
        }

        // Build Array fo moves to create
        MoveEffect[] moves = new MoveEffect[activeMons];
        // For each monster select a random move
        for(int monIndex = 0; monIndex < activeMons; monIndex++){
            // Select a random move
            int randInt = rand.nextInt(0,4); // Generates a random number between 0 and 3 (inclusive)
            // Set the random move
            moves[monIndex] = monsters[monIndex].getMoves()[randInt];

            // Select a random target
            int nextRandInt = rand.nextInt(0, turnInfoPackage.activeMon); // picks one of the monsters to target
            // Set the target
            String target = turnInfoPackage.convertIntToSlot(nextRandInt);
//            System.out.println("Target: " + target);
            moves[monIndex].setTarget(target);

            // Set the source
            String source = turnInfoPackage.convertIntToSlot(turnInfoPackage.getMonInTeam()+monIndex);
//            System.out.println("Source: " + source);
            moves[monIndex].setSource(source);
        }

        return moves;
    }

    public MoveEffect[] getRandomMonsterChoice(TurnInfoPackage turnInfoPackage) {
        //Get The activeMons
        int activeMons = turnInfoPackage.getActiveMon();
        int monInTeam = turnInfoPackage.getMonInTeam();
        int totalMons = turnInfoPackage.getMonCountTotal();

        // Build the array of Ai monsters that can be chosen from
        ArrayList<Monster> monsterArrayList = new ArrayList<Monster>();
        for (int i = activeMons; i < monInTeam; i++) {
            // Safety check to make sure we don't go over the total number of monsters
                // which shouldn't happen but just in case
            if(monInTeam+i >= totalMons){
               break;
            }
            Monster newMonster = turnInfoPackage.getMonsterBySlot(turnInfoPackage.convertIntToSlot(monInTeam+i));
            // If the monster is not dead add it to the list of dead monsters
            if(!newMonster.isDead()){
               monsterArrayList.add(newMonster);
            }
        }
        // If there are no monsters to choose from then do nothing
        if(monsterArrayList.isEmpty()){
            throw new IllegalArgumentException("No monsters are alive but getRandomMonsterChoice was called.");
        }

        // Now we have the list of monsters we can select from

        // Iterate over the active monsters and if they are dead then select a random monster
        // from the list of monsters that are not dead
        ArrayList<MoveEffect> swapMoves = new ArrayList<MoveEffect>();
        for(int monIndex = 0; monIndex < activeMons; monIndex++){
            // If the monster is dead then select a random monster from the list of monsters that are not dead
            if(turnInfoPackage.getMonsterBySlot(turnInfoPackage.convertIntToSlot(monInTeam+monIndex)).isDead()){
                // Select a random monster
                int randInt = rand.nextInt(0, monsterArrayList.size());
                // Set the random monster
                Queue<Effect> effectQueue = new LinkedList<>();
                Effect swapEffect = new Effect("None","Swap","", "Always","Swap Failed","Normal");
                effectQueue.add(swapEffect);
                MoveEffect swap = new MoveEffect("Swap", 5, 100, 0, "Normal", effectQueue);

                swap.setSource(turnInfoPackage.convertIntToSlot(monInTeam+monIndex));
                swap.setTarget(monsterArrayList.get(randInt).getSlot());
                swapMoves.add(swap);
            }
        }

        // The array will be empty if no monsters are dead but this will only run if no monsters are dead
        if(swapMoves.isEmpty()){
            throw new IllegalArgumentException("No monsters are dead but getRandomMonsterChoice was called.");
        }

        // Convert the ArrayList to an array
        return swapMoves.toArray(new MoveEffect[0]);
    }
}
