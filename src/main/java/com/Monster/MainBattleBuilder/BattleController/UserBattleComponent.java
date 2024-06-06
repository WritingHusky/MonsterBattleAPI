package com.Monster.MainBattleBuilder.BattleController;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserBattleComponent {
    private final ConcurrentHashMap<String, UserBattleInfo> userMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, UserBattleInfo> getUserMap() {
        return userMap;
    }

    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
    public void cleanupInactiveUsers() {

//        System.out.println("Cleaning up inactive users (UserBattleComponent)"); // Debugging

        LocalTime currentTime = LocalTime.now();

        // Use iterator to safely remove elements from ConcurrentHashMap
        Iterator<Map.Entry<String, UserBattleInfo>> iterator;
        iterator = userMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, UserBattleInfo> entry = iterator.next();
            UserBattleInfo userInfo = entry.getValue();

            // Check if the user hasn't updated the local time in the last 2 minutes
            if (Duration.between(userInfo.lastBattleAction, currentTime).toMinutes() > 2) {
                System.out.println("Removing user " + entry.getKey() + " from active battles");
                iterator.remove(); // Remove the user
            }
        }
    }
}
