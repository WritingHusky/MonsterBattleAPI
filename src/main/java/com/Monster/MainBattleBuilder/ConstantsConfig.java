package com.Monster.MainBattleBuilder;

import org.MonsterBattler.TurnDisplayElement;
import org.MonsterBattler.TurnDisplayElementFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicReference;

public class ConstantsConfig {
    public static final ResponseEntity<String> UnAuthorizedError = ResponseEntity.status(401)
            .contentType(MediaType.TEXT_PLAIN)
            .body("Client has not signed into Api");

    public static final ResponseEntity<String> EmptyInputError = ResponseEntity.status(422)
            .contentType(MediaType.TEXT_PLAIN).body("Received no content in request");

    public static final ResponseEntity<String> NotFoundError = ResponseEntity.status(401)
            .contentType(MediaType.TEXT_PLAIN)
            .body("Client has no entries in the api");

    public static final AtomicReference<ResponseEntity<String>> atomicResponse = new AtomicReference<>(new ResponseEntity<String>(HttpStatusCode.valueOf(520)));

    public static TurnDisplayElement createTurnStartDisplayElement(int turnNumber){
        return TurnDisplayElementFactory.create("Turn Header", "Turn: " + turnNumber);
    }

    public static final String welcomeMessage = "Welcome to monster Battle System for more information click on the Info button below.";

    public static final String webUrl = "http://localhost:5173";
//    public static final String webUrl = "http://localhost:3000";
    //TODO Add urls to the ConstantsConfig and replace the hardcoded urls in the API
}
