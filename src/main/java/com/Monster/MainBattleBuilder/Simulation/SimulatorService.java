package com.Monster.MainBattleBuilder.Simulation;

import org.MonsterBattler.BattleSimulator;
import org.MonsterBattler.DeadMonsterThrowable;
import org.MonsterBattler.TurnInfoPackage;
import org.springframework.stereotype.Service;

@Service
public class SimulatorService {
    public TurnInfoPackage simulateBattle(TurnInfoPackage turnInfoPackage) {
        // Ensure that input is Ready for simulating
        if (turnInfoPackage.state != TurnInfoPackage.State.Simulating){
//            System.out.println("Not Ready but: "+ turnInfoPackage.state);
            return turnInfoPackage;
        }
        // Now state is ready
        try {
            // Simulate
            BattleSimulator.executeTurn(turnInfoPackage); // Might throw DeadMonsterThrowable
            // Simulation is complete
            turnInfoPackage.state = TurnInfoPackage.State.Complete;
//            System.out.println("q post execute: " + turnInfoPackage.getTurnDisplayList().displayQ.size()); // Debugging
            return turnInfoPackage; // Send Back the TIP as the turn is complete

        } catch (DeadMonsterThrowable e) {
            // A monster has died so the pause the turn and wait for completion unless the game is over
//            System.out.println("Monster Died: (caught by simulator service) " + e.deadMonsterSlot);
//            System.out.println(turnInfoPackage.getTurnDisplayList().toString());

            // Check if the team is dead (i.e. The game is over)
            if (turnInfoPackage.isTeamDead(e.deadMonsterSlot)){
//                System.out.println("Team Dead: " + e.deadMonsterSlot);
                // If the game is over then end the turn
                turnInfoPackage.state = TurnInfoPackage.State.End;
                return turnInfoPackage;
            }
            // If the owner of the dead mon does not have a valid mon to switch to then keep going
            boolean allTeamatesDead = true;
            int deadMonTeam = turnInfoPackage.getMonsterBySlot(e.deadMonsterSlot).team;
            for(int i = turnInfoPackage.activeMon; i < turnInfoPackage.monInTeam; i++){
                int index = turnInfoPackage.monInTeam * deadMonTeam + i;
                if (!turnInfoPackage.getMonsterBySlot(turnInfoPackage.convertIntToSlot(index)).isDead){
//                    System.out.println("Not All Dead: " + e.deadMonsterSlot + " " + turnInfoPackage.convertIntToSlot(index));
                    allTeamatesDead = false;
                    break;
                }
            }
            // If they are all dead then continue the turn
            if(allTeamatesDead) {
//                System.out.println("All Dead: " + e.deadMonsterSlot);
                turnInfoPackage.state = TurnInfoPackage.State.Simulating;
                return simulateBattle(turnInfoPackage);
            }
            // Otherwise pause the turn to make the user switch monsters
            turnInfoPackage.state = TurnInfoPackage.State.Paused;

            return turnInfoPackage; //Send Back the TIP for the user to resolve the pause
        } catch (Exception e) {
            System.out.println("Unknown Error thrown: "+ e.getCause().getClass().getName());
            e.printStackTrace();
            // Send the name of the error back up the chain for debugging later
            return turnInfoPackage;
            // This should not stop the program
        }
    }
}
