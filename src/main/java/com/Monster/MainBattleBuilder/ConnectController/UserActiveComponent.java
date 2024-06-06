package com.Monster.MainBattleBuilder.ConnectController;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserActiveComponent {

    /**
     * Key = username<br>
     * Value = userID<br>
     * Being on the map implies active
     */
    private final ConcurrentHashMap<String, UserActiveInfo> activeUsers = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, UserActiveInfo> getActiveUsers() {
        return activeUsers;
    }

    public boolean hasUser( String userId){
        return getUserById(userId) != null;
    }

    public UserActiveInfo getUserById(String userId){
        Iterator<String> it = activeUsers.keys().asIterator();
        while(it.hasNext()){
            String activeKey = it.next();
            UserActiveInfo user = activeUsers.get(activeKey);
            if(Objects.equals(user.userId, userId)){
                return user;
            }
        }
        return null;
    }

    public String getJsonByUsername(String username){
        String userId = getActiveUsers().get(username).userId;
        return "{\"userId\": "+ userId+" }";
    }

    public String getJsonByUserId(String userId){
        return "{\"userId\": "+ userId+" }";
    }

    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
    public void cleanupInactiveUsers() {
        LocalTime currentTime = LocalTime.now();

//        System.out.println("Cleaning up inactive users (UserActiveComponent)"); // Debugging

        // Use iterator to safely remove elements from ConcurrentHashMap
        Iterator<Map.Entry<String, UserActiveInfo>> iterator;
        iterator = activeUsers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, UserActiveInfo> entry = iterator.next();
            UserActiveInfo userInfo = entry.getValue();

            // Check if the user hasn't updated the local time in the last 5 minutes
            if (Duration.between(userInfo.lastUserInteraction, currentTime).toMinutes() > 5) {
                iterator.remove(); // Remove the user
                System.out.println("Removed user: " + entry.getKey() + " from active users" + LocalTime.now());
            }
        }
    }
    public void UpdateUserInteraction(String userId){
        UserActiveInfo user = getUserById(userId);
        user.lastUserInteraction = LocalTime.now();
    }
}
