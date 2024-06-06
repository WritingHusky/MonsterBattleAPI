package com.Monster.MainBattleBuilder.BattleController;

import com.Monster.MainBattleBuilder.ConstantsConfig;
import org.MonsterBattler.TurnDisplayElement;
import org.MonsterBattler.TurnDisplayElementFactory;
import org.MonsterBattler.TurnInfoPackage;

import java.time.LocalTime;
import java.util.ArrayList;

public class UserBattleInfo {
    String battleId;
    TurnInfoPackage turnInfoPackage;
    int moveCount = 0;

    BattleRules rules;
    LocalTime lastBattleAction;

    ArrayList<TurnDisplayElement> turnLog;

    UserBattleInfo(String battleId, BattleRules rules){
        this.battleId = battleId;
        this.lastBattleAction = LocalTime.now();
        this.turnLog = new ArrayList<>();
        this.rules = rules;
        this.turnLog.add(ConstantsConfig.createTurnStartDisplayElement(0));
        this.turnLog.add(TurnDisplayElementFactory.create("Message Header", ConstantsConfig.welcomeMessage));
    }

    public String getTurnLogJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"TurnLog\": [");
        for(TurnDisplayElement element : this.turnLog) {
            jsonBuilder.append(element.toJson()).append(",");
        }
        // Remove the trailing comma if there are elements in the queue
        if (!turnLog.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("]}");
        return jsonBuilder.toString();
    }
}
