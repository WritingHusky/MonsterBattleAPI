package com.Monster.MainBattleBuilder;

import com.Monster.MainBattleBuilder.Simulation.SimulatorService;
import org.MonsterBattler.TurnInfoPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class APIService {

    private final WebClient webClient;

    @Autowired
    private SimulatorService simulatorService;

    public APIService() {
        this.webClient = WebClient.create();
    }

    public Mono<String> callAiAPI(String destination, String jsonBody) {
        return webClient.post()                          // Specify HTTP POST method
                .uri("http://localhost:8081/api-v1/ai/" + destination)   // Specify the URI
                .contentType(MediaType.APPLICATION_JSON) // Set Content-Type header
                .body(BodyInserters.fromValue(jsonBody)) // Set the request body
                .retrieve()                             // Perform the request
                .bodyToMono(String.class);               // Convert the response body to Mono<String>
    }
    public Mono<TurnInfoPackage> callSimAPI(TurnInfoPackage turnInfoPackage) {
        return Mono.just(simulatorService.simulateBattle(turnInfoPackage));


    }
    public Mono<String> callMonsterDB(String jsonBody, String destination){
        return webClient.post()
                .uri("http://localhost:8080/api/monster/" + destination)  //TODO Replace with the specific endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonBody))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> callTeamDB(String jsonBody, String destination){
        return webClient.post()
                .uri("http://localhost:8080/api/team/" + destination)  //TODO Replace with the specific endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonBody))
                .retrieve()
                .bodyToMono(String.class);
    }
}
