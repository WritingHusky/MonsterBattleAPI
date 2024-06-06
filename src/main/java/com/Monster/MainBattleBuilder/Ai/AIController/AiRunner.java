package com.Monster.MainBattleBuilder.Ai.AIController;

import com.Monster.MainBattleBuilder.Ai.AIs.RandomAI;
import org.MonsterBattler.MoveEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This will be the service that actually does the Ai piece
 */
@Service
public class AiRunner {


    private final RandomAI randomAI;

    @Autowired
    public AiRunner(RandomAI randomAI) {
        this.randomAI = randomAI;
    }

    /**
     * This is where the actual running of the AI will start/initiate
     * @param aiInfo the info that will be need to simulate
     * @return The moves that are chosen by the AI
     */
    public MoveEffect[] runAiMoves(AiInfo aiInfo){
        //TODO Add some handling

        MoveEffect[] moves = new MoveEffect[0];
        //Chose the run method
        switch (aiInfo.opponent){
            case Random -> moves = randomAI.getRandomMoveChoice(aiInfo.turnInfoPackage);
        }
        return moves;
    
    }

    public MoveEffect[] runAiMonster(AiInfo aiInfo) {
        switch (aiInfo.opponent) {
            case Random -> {
                return randomAI.getRandomMonsterChoice(aiInfo.turnInfoPackage);
            }
            default -> throw new IllegalArgumentException("Invalid AI type");
        }
    }
}
