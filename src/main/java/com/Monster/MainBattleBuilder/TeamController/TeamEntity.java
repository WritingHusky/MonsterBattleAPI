package com.Monster.MainBattleBuilder.TeamController;

import jakarta.persistence.*;

import java.lang.reflect.Array;

@Entity
@Table(name = "Team")
public class TeamEntity {
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

    public TeamEntity(String teamId, int monster1Id, int monster2Id, int monster3Id,
                      int monster4Id, int monster5Id, int monster6Id) {
        this.username = teamId;

        this.mon1DexId = monster1Id;

        this.mon2DexId = monster2Id;

        this.mon3DexId = monster3Id;

        this.mon4DexId = monster4Id;

        this.mon5DexId = monster5Id;

        this.mon6DexId = monster6Id;

    }

    public TeamEntity(String username) {
        this.username = username;
    }

    public TeamEntity() {
    }

    public String toJson(){
        /*
        * {
        *   monsters: [
        *
        *   ]
        * }
        * */

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

    } //TODO Build out toJSon() (need react usage)

    @Override
    public String toString() {
        return "TeamEntity{" +
                "username='" + username + '\'' +
                ", mon1DexId=" + mon1DexId +
                ", mon2DexId=" + mon2DexId +
                ", mon3DexId=" + mon3DexId +
                ", mon4DexId=" + mon4DexId +
                ", mon5DexId=" + mon5DexId +
                ", mon6DexId=" + mon6DexId +
                '}';
    }

    public int[] toArray() {
        return new int[]{mon1DexId, mon2DexId, mon3DexId, mon4DexId, mon5DexId, mon6DexId};
    }
}
