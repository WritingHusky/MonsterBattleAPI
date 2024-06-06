package com.Monster.MainBattleBuilder.Ai.Teams;

import jakarta.persistence.*;

@Entity
@Table(name = "AiTeams")
public class AITeamEntity {
    @Id
    private String username;

    @Column(name = "mon1DexId")
    private int mon1DexId;

    @Column(name = "mon2DexId")
    private int mon2DexId;

    @Column(name = "mon3DexId")
    private int mon3DexId;

    @Column(name = "mon4DexId")
    private int mon4DexId;

    @Column(name = "mon5DexId")
    private int mon5DexId;

    @Column(name = "mon6DexId")
    private int mon6DexId;

    public AITeamEntity(String teamId, int monster1Id, int monster2Id, int monster3Id,
                        int monster4Id, int monster5Id, int monster6Id) {
        this.username = teamId;

        this.mon1DexId = monster1Id;

        this.mon2DexId = monster2Id;

        this.mon3DexId = monster3Id;

        this.mon4DexId = monster4Id;

        this.mon5DexId = monster5Id;

        this.mon6DexId = monster6Id;

    }

    public AITeamEntity(String username) {
        this.username = username;
    }

    public AITeamEntity() {
    }

    public String toJson(){

        String monsterStr = "";

        monsterStr += mon1DexId + ",";
        monsterStr += mon2DexId + ",";
        monsterStr += mon3DexId + ",";
        monsterStr += mon4DexId + ",";
        monsterStr += mon5DexId + ",";
        monsterStr += mon6DexId;

        return "{" +
                "\"Monsters\":" + "[" +
                monsterStr +
                "]" +
                "}";

    }

    public int[] toArray() {
        return new int[]{mon1DexId, mon2DexId, mon3DexId, mon4DexId, mon5DexId, mon6DexId};
    }

}
