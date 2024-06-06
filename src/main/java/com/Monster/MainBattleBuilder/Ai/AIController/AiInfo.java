package com.Monster.MainBattleBuilder.Ai.AIController;

import org.MonsterBattler.MoveEffect;
import org.MonsterBattler.TurnInfoPackage;

public class AiInfo {

    public AiInfo( String opponent, TurnInfoPackage turnInfoPackage) {

        //Add To Switch as more AIs are created
        //noinspection SwitchStatementWithTooFewBranches
        switch (opponent){
            case "Random":
            default:
                this.opponent = Opponent.Random;
                break;
        }
        this.turnInfoPackage = turnInfoPackage;
    }

     public enum Opponent {
        Random
    }
    public Opponent opponent;
    public MoveEffect[] resultMoves;
    public TurnInfoPackage turnInfoPackage;
}
