package com.Monster.MainBattleBuilder.BattleController;

public class BattleConnectCredentials {
    String userId;
    String opponent;
    BattleRules rules;
    String username;

    public BattleConnectCredentials(String userId, String opponent, BattleRules rules, String username) {
        this.userId = userId;
        this.opponent = opponent;
        this.rules = rules;
        this.username = username;
    }

    @Override
    public String toString() {
        return "BattleConnectCredentials{" +
                "userId='" + userId + '\'' +
                ", opponent='" + opponent + '\'' +
                ", rules=" + rules +
                '}';
    }
}
