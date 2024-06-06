package com.Monster.MainBattleBuilder.BattleController;

public class BattleRules {
    int totalMonCount;
    int teamCount = 2;
    int activeMon;

    int version;
    String name;


    public BattleRules(int totalMonCount, int teamCount, int activeMon, int version, String name) {
        this.totalMonCount = totalMonCount;
        this.teamCount = teamCount;
        this.activeMon = activeMon;
        this.version = version;
        this.name = name;
    }

    @Override
    public String toString() {
        return "BattleRules{" +
                "totalMonCount=" + totalMonCount +
                ", teamCount=" + teamCount +
                ", activeMon=" + activeMon +
                ", version=" + version +
                ", name='" + name + '\'' +
                '}';
    }
}
