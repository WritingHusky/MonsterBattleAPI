package com.Monster.MainBattleBuilder.MoveDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.MonsterBattler.Effect;
import org.MonsterBattler.MoveEffect;

import java.util.Queue;

@Entity
@Table(name = "moves")
public class MoveEntity {

    @Id
    @Column(name = "MoveId")
    public int moveId;

    @Column(name = "MoveName")
    public String moveName;

    @Column(name = "priority")
    public int priority;

    @Column(name = "accuracy")
    public int accuracy;

    @Column(name = "power")
    public int power;

    @Column(name = "typing")
    public String typing;

    public MoveEffect toMove(Queue<Effect> effectQueue){
        return new MoveEffect(moveName, priority, accuracy, power,typing, effectQueue);
    }
}
