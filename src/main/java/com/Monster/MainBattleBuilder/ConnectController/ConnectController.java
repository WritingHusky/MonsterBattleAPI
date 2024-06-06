package com.Monster.MainBattleBuilder.ConnectController;

import com.Monster.MainBattleBuilder.ConstantsConfig;
import com.Monster.MainBattleBuilder.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/connect")
@CrossOrigin(origins = ConstantsConfig.webUrl)
public class ConnectController {

    private final UserActiveComponent userActiveComponent;

    @Autowired
    private UserConnectionService userConnectionService;

    @Autowired
    ConnectController(UserActiveComponent userActiveComponent){
        this.userActiveComponent = userActiveComponent;
    }

    /**
     * The way for the user to become active and validate their information
     * @param credentials the information that the user uses
     * @return The confirmation of becoming an active user
     */
    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody Credentials credentials){
        //Check that data was sent
        if(credentials == null){
            return ConstantsConfig.EmptyInputError;
        } if (Objects.equals(credentials.username, "") || Objects.equals(credentials.password, "")){
            return ConstantsConfig.EmptyInputError;
        }

        //Validate Username and Password
        if(!userConnectionService.validateUser(credentials.username, credentials.password)){
//            System.out.println("Invalid Username or Password");
            return ConstantsConfig.UnAuthorizedError;
        }

        //Check if the user is already / still signed in
        if (userActiveComponent.getActiveUsers().containsKey(credentials.username)){
            System.out.println("User already signed in");
            return ResponseEntity.ok().body(userActiveComponent.getJsonByUsername(credentials.username));
        }

        //Add user to the active user list
        userActiveComponent.getActiveUsers().put(credentials.username, new UserActiveInfo(generateUniqueUserKey()));
//        System.out.println("User Signed In: " + credentials.username );

        //Error Handling
        if(!userActiveComponent.getActiveUsers().containsKey(credentials.username)){
            return ResponseEntity.internalServerError().body("Username entered but not found");
        }
        if (userActiveComponent.getActiveUsers().get(credentials.username).userId.isEmpty()){
            return ResponseEntity.internalServerError().body("Username entered but userId was not found");
        }
//        System.out.println("User Signed In");
        return ResponseEntity.ok(userActiveComponent.getActiveUsers().get(credentials.username).userId);
    }

    /**
     * The way to add the user to the DB for them to sign in
     * @param newCredentials The credentials that they would like to use to sign in
     * @return The confirmation of signup request
     */
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody Credentials newCredentials){
        //Check credentials exist
        if (newCredentials == null){
            return ConstantsConfig.EmptyInputError;
        } if (Objects.equals(newCredentials.username, "") || Objects.equals(newCredentials.password, "")){
            return ConstantsConfig.EmptyInputError;
        }

        //Check If username is taken
        if (userConnectionService.hasUser(newCredentials.username)){
            return ConstantsConfig.NotFoundError;
        }

        if(userConnectionService.addUser(newCredentials.username, newCredentials.password)){
            //Now that the user is in the database sign them in
            userActiveComponent.getActiveUsers().put(newCredentials.username, new UserActiveInfo(generateUniqueUserKey()));

            //Send Back the UserID
            return ResponseEntity.ok(userActiveComponent.getJsonByUsername(newCredentials.username));
        } else {
            return ResponseEntity.internalServerError().body("Could not enter into database");
        }
    }

    /**
     * The way the user removes themselves from the
     * @param credentials The info of the user that would like to log out
     * @return The confirmation that the user signed out
     */
    @PostMapping("/logOut")
    public ResponseEntity<String> logOut(@RequestBody Credentials credentials){

        if(credentials == null){
            return ConstantsConfig.EmptyInputError;
        } if(!userActiveComponent.getActiveUsers().containsKey(credentials.username)){
            return ResponseEntity.accepted().body("Already Logged Out");
        } if(!userConnectionService.validateUser(credentials.username, credentials.password)){
            return ConstantsConfig.UnAuthorizedError;
        }

        userActiveComponent.getActiveUsers().remove(credentials.username);
        return ResponseEntity.ok("logged Out");

    }

    /**
     * The way to request the information about a user. <br>
     * Users have more access to their data then others
     * @param credentials The info of the user that would like information
     * @return The set of information about the given user
     */
    @PostMapping("/info")
    public ResponseEntity<String> requestInfo(@RequestBody Credentials credentials){
        if (credentials == null) {
            return ConstantsConfig.EmptyInputError;
        } if (!userConnectionService.validateUser(credentials.username, credentials.password)){
            return ConstantsConfig.UnAuthorizedError;
        }
        try {
            UserEntity user = userConnectionService.getUserByUserName(credentials.username);
            return ResponseEntity.ok(user.toJson());
        } catch (NoSuchElementException e){
            return ResponseEntity.badRequest().body("User does not exist");
        }

    }
    @PostMapping("/ping")
    public ResponseEntity<String> pingUser(@RequestBody PingCredentials credentials){
        // Check that data was sent
        if (credentials == null || credentials.userId.isEmpty()){
            return ConstantsConfig.EmptyInputError;
        } if (!userActiveComponent.hasUser(credentials.userId)){
            return ConstantsConfig.UnAuthorizedError;
        }

        // Update the last interaction time
        userActiveComponent.getUserById(credentials.userId).lastUserInteraction = LocalTime.now();
        userActiveComponent.UpdateUserInteraction(credentials.userId);

        // Return the user's ID
        return ResponseEntity.ok(userActiveComponent.getJsonByUserId(credentials.userId));
    }

    private String generateUniqueUserKey() {
        String key;
        do {
            key = UUID.randomUUID().toString();
        } while (isKeyInUse(key));

        return key;
    }
    //Separate to address lambda issues will key to check not being final
    private boolean isKeyInUse(String keyToCheck) {
        return userActiveComponent.getActiveUsers().values().stream().anyMatch(UserActiveInfo -> keyToCheck.equals(UserActiveInfo.userId));
    }

}
