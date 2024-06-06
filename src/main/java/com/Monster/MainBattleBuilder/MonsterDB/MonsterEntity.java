package com.Monster.MainBattleBuilder.MonsterDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.google.gson.Gson;
import org.MonsterBattler.Monster;
import org.MonsterBattler.MoveEffect;

@Entity
@Table(name = "Monsters")
public class MonsterEntity {

    @Id
    @Column(name = "DexID")
    public int dexId;

    @Column(name= "Name")
    public String name;

    @Column(name = "Hp")
    private int hp;

    @Column(name = "Atk")
    private int atk;

    @Column(name = "Def")
    private int def;

    @Column(name = "SpA")
    private int spa;

    @Column(name = "Spd")
    private int spd;

    @Column(name = "Speed")
    private int speed;

    @Column(name = "Type")
    private String type;

    @Column(name = "Move1")
    private int move1;

    @Column(name = "Move2")
    private int move2;

    @Column(name = "Move3")
    private int move3;

    @Column(name = "Move4")
    private int move4;


    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Monster toMonster(int level, MoveEffect[] moves){
        int[] stats = {0};
        return new Monster(name, dexId, stats, level, type, moves);
    }
    public int[] getMoveIds(){
        return new int[]{move1, move2, move3, move4};
    }
}
