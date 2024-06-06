package com.Monster.MainBattleBuilder.MoveDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.MonsterBattler.Effect;

@Entity
@Table(name = "moveeffects")
public class EffectEntity {

    @Id
    @Column(name = "effectId")
    public int effectId;

    @Column(name = "Trigger_code")
    public String triggerCode;

    @Column(name = "FailedTriggerMsg")
    public String failedTriggerMsg;

    @Column(name = "ResultCode")
    public String resultCode;

    @Column(name = "EffectValue")
    public String effectValue;

    @Column(name = "AttackType")
    public String attackType;

    public Effect toEffect(String moveType){
        return new Effect(attackType, resultCode,effectValue, triggerCode,failedTriggerMsg, moveType);
    }
}
