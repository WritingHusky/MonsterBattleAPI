package com.Monster.MainBattleBuilder.ConnectController;

import java.time.LocalTime;

public class UserActiveInfo {
    String userId;
    LocalTime lastUserInteraction;

    public UserActiveInfo(String userId) {
        this.userId = userId;
        this.lastUserInteraction = LocalTime.now();
    }
}
