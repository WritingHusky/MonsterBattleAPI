package com.Monster.MainBattleBuilder.MoveDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class InteractionEntity {
    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "moveId")
    public int moveId;

    @Column(name = "effectId")
    public int effectId;

}
