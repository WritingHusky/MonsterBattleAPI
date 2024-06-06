package com.Monster.MainBattleBuilder.Ai.AIController;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiComponent {

    /**
     * Key = battleId
     * value = AiInfo
     */
    private final ConcurrentHashMap<String, AiInfo> battleMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, AiInfo> getBattleMap() { return battleMap; }



}
