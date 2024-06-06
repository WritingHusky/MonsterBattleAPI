package com.Monster.MainBattleBuilder.BattleController;

public class MoveCredentials {

    String userId;
    String battleId;
    String sourceSlot;
    String targetSlot;
    int moveIndex;


    public MoveCredentials() {
    }

    public MoveCredentials(String userId, String battleId, String sourceSlot, String targetSlot, int moveIndex) {
        this.userId = userId;
        this.battleId = battleId;
        this.sourceSlot = sourceSlot;
        this.targetSlot = targetSlot;
        this.moveIndex = moveIndex;
    }

    @Override
    public String toString() {
        return "MoveCredentials{" +
                "userId='" + userId + '\'' +
                ", battleId='" + battleId + '\'' +
                ", sourceMonsterCode='" + sourceSlot + '\'' +
                ", targetMonsterCode='" + targetSlot + '\'' +
                ", moveIndex=" + moveIndex +
                '}';
    }
}
