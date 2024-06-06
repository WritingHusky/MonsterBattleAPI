package com.Monster.MainBattleBuilder.Ai.AIController;


import com.Monster.MainBattleBuilder.Ai.Teams.AITeamEntity;
import com.Monster.MainBattleBuilder.Ai.Teams.AiTeamService;
import org.MonsterBattler.MoveEffect;
import org.MonsterBattler.TurnInfoPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class AiService {

    private final AiComponent aiComponent;
    @Autowired
    private AiRunner aiRunner;
    @Autowired
    private AiTeamService teamService;

    @Autowired
    AiService(AiComponent aiComponent) {
        this.aiComponent = aiComponent;
    }

    /**
     * This will be the method that will be used to start a battle in the AI
     * @param battleId the id of the battle
     * @param opponent the code of the opponent
     * @param turnInfoPackage the information about the Battle
     * @throws IllegalArgumentException the battleId, opponent, or turnInfoPackage is null
     * @throws RuntimeException the AiInfo was created but not found
     */
    public void startBattle(String battleId,
                            String opponent,
                            TurnInfoPackage turnInfoPackage) throws IllegalArgumentException, RuntimeException{

        if (battleId == null) {
            throw new IllegalArgumentException("No battleId sent");
        } else if (opponent == null) {
            throw new IllegalArgumentException("No opponent sent");
        } else if (turnInfoPackage == null) {
            throw new IllegalArgumentException("No turnInfoPackage sent");
        }

        if (aiComponent.getBattleMap().containsKey(battleId)) {
           return; // Battle already started
        }

        // Add the new info into the map
        AiInfo newAiInfo = new AiInfo(opponent, turnInfoPackage);
        aiComponent.getBattleMap().put(battleId, newAiInfo);

        if (!aiComponent.getBattleMap().containsKey(battleId))
            throw new RuntimeException("AiInfo created but not found");
    }


    public boolean endBattle(String battleId) {

        // Validate the battleId
        if (battleId == null) {
            throw new IllegalArgumentException("No battleId sent");
        } else if (!aiComponent.getBattleMap().containsKey(battleId)) {
            return false; // Could not find the battle
        }

        aiComponent.getBattleMap().remove(battleId);
        return true;
    }


    /**
     * This will be the highest level method that will start the AI process
     * @param battleId the id of the battle
     * @return the moves that the AI has chosen
     */
    public Mono<MoveEffect[]> getMoveEffects(String battleId) {

        // Validate the battleId
        if (battleId == null) {
            throw new IllegalArgumentException("No battleId sent");
        } else if (!aiComponent.getBattleMap().containsKey(battleId)) {
            throw new IllegalArgumentException("Battle Not Started in AI API");
        }

        // Get the AiInfo
        AiInfo aiInfo = aiComponent.getBattleMap().get(battleId);

        // Run the Ai according to the supplied information
        return Mono.just(aiRunner.runAiMoves(aiInfo));

    }

    //TODO rework
    /**
     * This will be the method that will be used to request a team from the AI
     *
     * @param opponent the code of the opponent
     * @return the team that was requested
     * @throws IllegalArgumentException something is wrong getting the opponents team
     * @throws RuntimeException         if the team is in the table but was not read
     */
    public AITeamEntity requestTeam(String opponent) throws IllegalArgumentException, RuntimeException {
        if (Objects.equals(opponent, "") || opponent == null) {
            throw new IllegalArgumentException("No opponent sent");
        }

        // Validate the opponent
        switch (opponent) {
            case "Random":
                break;
            default:
                throw new IllegalArgumentException("Invalid opponent");
        }

        //Check if the team is in the table
        if (!teamService.isTeamInTable(opponent)) {
            throw new IllegalArgumentException("Team is not in table");
        }
        //Retrieve the team data
        if (teamService.readTeam(opponent).isPresent()) {
            return teamService.readTeam(opponent).get();
        } else {
            throw new RuntimeException("Team is in table but was not read");
        }
    }

    public Mono<MoveEffect[]> getMonsterSwap(String battleId) {
        // Validate the battleId
        if (battleId == null) {
            throw new IllegalArgumentException("No battleId sent");
        } else if (!aiComponent.getBattleMap().containsKey(battleId)) {
            throw new IllegalArgumentException("Battle Not Started in AI API");
        }

        // Get the AiInfo
        AiInfo aiInfo = aiComponent.getBattleMap().get(battleId);
        // Run the Ai according to the supplied information
        return Mono.just(aiRunner.runAiMonster(aiInfo));

    }
}
// TODO Add cleanup to the aiComponent just in case the battle builder doesn't do it (Maybe losses connection etc...)

/*
Things that might change:
- might want to make AI run before client makes their move choice
    - This will require a different AI structure as branch factor would be 81^2 for one turn with MCTS
 */
