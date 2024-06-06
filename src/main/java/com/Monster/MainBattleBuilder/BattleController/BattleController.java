package com.Monster.MainBattleBuilder.BattleController;

import com.Monster.MainBattleBuilder.APIService;
import com.Monster.MainBattleBuilder.Ai.AIController.AiService;
import com.Monster.MainBattleBuilder.ConnectController.PingCredentials;
import com.Monster.MainBattleBuilder.ConstantsConfig;
import com.Monster.MainBattleBuilder.MonsterDB.MonsterEntity;
import com.Monster.MainBattleBuilder.MonsterDB.MonsterService;
import com.Monster.MainBattleBuilder.MoveDB.MoveService;
import com.Monster.MainBattleBuilder.Ai.Teams.AITeamEntity;
import com.Monster.MainBattleBuilder.Ai.Teams.AiTeamService;
import com.Monster.MainBattleBuilder.TeamController.TeamEntity;
import com.Monster.MainBattleBuilder.TeamController.TeamService;
import com.google.gson.Gson;
import org.MonsterBattler.*;

import java.util.logging.Logger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

import static org.MonsterBattler.TurnInfoPackage.State.*;

@RestController
@RequestMapping("/api/battle")
@CrossOrigin(origins = ConstantsConfig.webUrl)
public class BattleController {

    @Autowired
    APIService apiService;
    @Autowired
    TeamService teamService = new TeamService();
    @Autowired
    AiTeamService aiTeamService = new AiTeamService();
    @Autowired
    MonsterService monsterService = new MonsterService();
    @Autowired
    MoveService moveService = new MoveService();
    @Autowired
    AiService aiService;

    private final UserBattleComponent userBattleComponent;
    Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(BattleController.class.getName());


    final int level = 1; // Currently hardcoded

    @Autowired
    BattleController(UserBattleComponent userBattleComponent){
        this.userBattleComponent = userBattleComponent;
    }

    //TODO Add a way to check userID (probably check through the connect controller)
    /**
     * @param credentials The Information required to validate the user (opponent type?)
     * @return ResponseEntity with the unique code that will be associated with the battle that the user is a part of
     */
    @PostMapping("/connect")
    public ResponseEntity<String> connectToBattleSimple(@RequestBody BattleConnectCredentials credentials){
        // Check if the credentials are valid
        if (credentials == null) {
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        } else if (credentials.userId == null) {
            return ResponseEntity.unprocessableEntity().body("User Id not sent");
        } else if (credentials.opponent == null || credentials.opponent.isEmpty()) {
            return ResponseEntity.unprocessableEntity().body("Opponent not sent");
        } else if (credentials.rules == null) {
            return ResponseEntity.unprocessableEntity().body("Rules not sent");
        }

        // Check if the user is already in a battle
        if (userBattleComponent.getUserMap().containsKey(credentials.userId)) {
            // If the user is already in the battle table give them the battle ID their current battle
            return ResponseEntity.accepted().body(userBattleComponent.getUserMap().get(credentials.userId).battleId);
        }

        // Generate a new battle ID
        String newBattleId = generateNewBattleKey();
        // Create a new UserBattleInfo
        UserBattleInfo newUBI = new UserBattleInfo(newBattleId, credentials.rules);
        // Create a new TurnInfoPackage
        TurnInfoPackage newTIP = new TurnInfoPackage(
                credentials.rules.totalMonCount,
                credentials.rules.teamCount,
                credentials.rules.activeMon);
        newTIP.fillMonSlots();
        // Create the monsters that will battle

        // Get the TeamEntities
        Optional<TeamEntity> teamOptional = teamService.readTeam(credentials.username);
        if(teamOptional.isEmpty())
            return ResponseEntity.unprocessableEntity().body("Team Not created");
        TeamEntity playerTeam = teamOptional.get();

        Optional<AITeamEntity> opponentTeamOptional = aiTeamService.readTeam(credentials.opponent);
        if(opponentTeamOptional.isEmpty())
            return ResponseEntity.unprocessableEntity().body("Ai Team Not created");
        AITeamEntity aiTeam = opponentTeamOptional.get();

        // Convert to int[]
        int[] playerDexIds = playerTeam.toArray();
        int[] opponentDexIds = aiTeam.toArray();

        // Build out the player monsters
        Monster[] playerMonsters = new Monster[playerDexIds.length];
        for (int playerIndex = 0; playerIndex < playerDexIds.length; playerIndex++) {
            playerMonsters[playerIndex] = monsterFromDexId(playerDexIds[playerIndex]);
        }

        // Build out the opponent monsters
        Monster[] opponentMonsters = new Monster[opponentDexIds.length];
        for (int opponentIndex = 0; opponentIndex < opponentDexIds.length; opponentIndex++) {
            opponentMonsters[opponentIndex] = monsterFromDexId(opponentDexIds[opponentIndex]);
        }

        //Combine the arrays
        Monster[] allMonsters = new Monster[playerMonsters.length + opponentMonsters.length];
        System.arraycopy(playerMonsters, 0, allMonsters, 0, playerMonsters.length);
        System.arraycopy(opponentMonsters, 0, allMonsters, playerMonsters.length, opponentMonsters.length);

        // Set the monsters in the TIP
        newTIP.setMonsters(allMonsters);

        // Set the TIP into the User Info
        newUBI.turnInfoPackage = newTIP;
        newUBI.rules = credentials.rules;

        // Add user to the Battles
        userBattleComponent.getUserMap().put(credentials.userId, newUBI);

        // Create the AiInfo
        aiService.startBattle(newBattleId, credentials.opponent, newTIP);

        // Return the new Battle ID
        return ResponseEntity.ok(newBattleId);
    }

    /**
     * A Way to access the TIP of an active battle.
     * @param credentials The credentials of the account that would like the T.I.P. of.
     * @return The T.I.P. That is associated with the given account <br>
     * The Request will fail if the requested account has not signed in or has no battle associated with it
     */
    @PostMapping("/retrieveTIP")
    public ResponseEntity<String> retrieveTIP(@RequestBody BattleCredentials credentials){
        if (credentials == null ){
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        } else if(credentials.userId == null ){
            return ResponseEntity.unprocessableEntity().body("No UserId");
        } else if(credentials.userId.isEmpty() ){
            return ResponseEntity.unprocessableEntity().body("UserId Empty");
        }
        updateLastAction(credentials.userId);

        if(!userBattleComponent.getUserMap().containsKey(credentials.userId)){ //If the user is not in the userMap tell them
            return ResponseEntity.badRequest().body("User not in a battle");
        }

        // Get the info about the battle for the user
        UserBattleInfo userBattleInfo = userBattleComponent.getUserMap().get(credentials.userId);
        if (userBattleInfo.turnInfoPackage == null){
            return ResponseEntity.status(404).body("TIP not created");
        }
        return ResponseEntity.ok(userBattleInfo.turnInfoPackage.toJson());
    }

    /**
     * A way to receive the moves for a battle
     * @param requestBody The credentials needed.
     * @return A response will contain the status of the MoveQueue (need more moves, ready to sim, etc...)
     */
    @PostMapping("/sendMove")
    public ResponseEntity<String> receiveMoves(@RequestBody String requestBody){
//        logger.info("Received request to /sendMove endpoint");
//        logger.info("Request Body: {}", requestBody);

        // Convert the request body to MoveCredentials
        MoveCredentials credentials = new Gson().fromJson(requestBody, MoveCredentials.class);


        if (credentials == null){
            logger.warning("Credentials are null");
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        }
//        logger.info("Credentials: {}", credentials);

        if (credentials.userId == null){
            logger.warning("UserId is null");
            return ResponseEntity.unprocessableEntity().body("No UserId");
        } else if (credentials.battleId == null){
            logger.warning("BattleId is null");
            return ResponseEntity.unprocessableEntity().body("No BattleId");
        } else if (credentials.sourceSlot == null || credentials.sourceSlot.isEmpty()) {
            logger.warning("sourceSlot is null or empty");
            return ResponseEntity.unprocessableEntity().body("No sourceMonsterCode");
        } else if (credentials.targetSlot == null || credentials.targetSlot.isEmpty()) {
            logger.warning("targetSlot is null or empty");
            return ResponseEntity.unprocessableEntity().body("No targetMonsterCode");
        } else if (credentials.moveIndex < 0) {
            logger.warning("moveIndex is less than 0");
            return ResponseEntity.unprocessableEntity().body("Invalid moveIndex");
        }

        //Validation of Move later
        if(!userBattleComponent.getUserMap().containsKey(credentials.userId)) {
            logger.warning("UserId not found in userMap");
            return ResponseEntity.badRequest().body("Not Signed In");
        }
        UserBattleInfo userBattleInfo = userBattleComponent.getUserMap().get(credentials.userId);
        if(!Objects.equals(userBattleInfo.battleId, credentials.battleId)){
            return ResponseEntity.badRequest().body("Wrong BattleID expected: " + userBattleInfo.battleId + " received: " + credentials.battleId);
        }

        updateLastAction(credentials.userId);

        // get the number of active monsters ( to handle dead teammates)
        int activeMon = 0;
        for(int i = 0; i < userBattleInfo.rules.activeMon; i++){
            if(!userBattleInfo.turnInfoPackage.getMonsterBySlot(userBattleInfo.turnInfoPackage.convertIntToSlot(i)).isDead){
                activeMon++;
            }
        }

        // If the moveIndex is 5 then it is a swap move
        if(credentials.moveIndex == 4) {
            // Verify that the TIP state is paused
            if (userBattleInfo.turnInfoPackage.state != Paused) {
                return ResponseEntity.accepted().body("Not in a state to swap");
            }
            // End of verification?
        // Check if the move Queue is full // Skip this step if the move is a swap
        } else if(userBattleInfo.moveCount >= activeMon){
            return ResponseEntity.accepted().body("Move Queue is full");
        }

        MoveEffect newMove = userBattleInfo.turnInfoPackage.getMonsterBySlot(credentials.sourceSlot)
                .moves[credentials.moveIndex];

        newMove.setSource(credentials.sourceSlot);
        newMove.setTarget(credentials.targetSlot);

        // Setup the move queue
        MoveQueueBuilder moveQueueBuilder = new MoveQueueBuilder();
        moveQueueBuilder.setMoveQueue(userBattleInfo.turnInfoPackage.getMoveQueue());

        // Push the move into the queue
        moveQueueBuilder.pushMove(newMove,userBattleInfo.turnInfoPackage);

        TurnInfoPackage newTIP = userBattleInfo.turnInfoPackage;
        newTIP.setTurnDisplayList(new TurnDisplayList());
        newTIP.setMoveQueue(moveQueueBuilder.getMoveQueue());

        userBattleInfo.turnInfoPackage = newTIP;

        // If the move is a swap move, move count is not needed
        if(credentials.moveIndex == 4){
            // Resume the battle
            userBattleInfo.turnInfoPackage.state = Resume;
            // Replace the UBI
            userBattleComponent.getUserMap().replace(credentials.userId, userBattleInfo);
            // Let the user know that the move is received
            return ResponseEntity.ok("Swap Move Received");
        }

        userBattleInfo.moveCount++;

//        logger.info("Move Received moveCount is now: " + userBattleInfo.moveCount);

        // Check if the move count is not yet full
        if (userBattleInfo.moveCount < activeMon){
            userBattleComponent.getUserMap().replace(credentials.userId, userBattleInfo);
            return ResponseEntity.ok("Move Received");
        }
        // Now the move Queue is full
//            logger.info("Move Queue filled at count:" + userBattleInfo.moveCount);
        userBattleInfo.turnInfoPackage.state = Waiting;

        // Before saying that the move queue is filled, we need to start the process of generating the opponents move
        aiService.getMoveEffects(userBattleInfo.battleId)
                // When the Ai's moves are received
                .subscribe(aiMoves -> {
                    // Get the TIP
                    TurnInfoPackage tip = userBattleInfo.turnInfoPackage;
                    // Set the moveQueueBuilder
                    MoveQueueBuilder aiMoveQueueBuilder = new MoveQueueBuilder();
                    aiMoveQueueBuilder.moveQueue = tip.getMoveQueue();
                    // Push Moves to the Queue
                    for(MoveEffect aiMove: aiMoves){
                        aiMoveQueueBuilder.pushMove(aiMove, tip);
                    }
                    userBattleInfo.turnInfoPackage.moveQueue = aiMoveQueueBuilder.moveQueue;

                    // Set the state to ready
                    userBattleInfo.turnInfoPackage.state = Ready;
                    userBattleComponent.getUserMap().replace(credentials.userId, userBattleInfo);
                }, error -> {
                    // Handle any errors
                    System.err.println("Error occurred: (Ai Move) " + error.getMessage());
                    error.printStackTrace();
//                    System.out.println(userBattleInfo.turnInfoPackage.moveQueue.toString());
                });
        // Send The response to the user while the Ai is processing
        userBattleComponent.getUserMap().replace(credentials.userId, userBattleInfo);
        return  ResponseEntity.accepted().body("Move Queue filled");

    }

    /**
     * This is a general way to inquire about the status of a given match.
     * @param credentials The userId and BattleID that would like to be pinged
     * @return The State of the battle / user
     */
    @PostMapping("/inquire")
    public ResponseEntity<String> ping(@RequestBody BattleCredentials credentials){
        // Validate credentials
        if (credentials == null){
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        } else if (credentials.userId == null){
            return ResponseEntity.unprocessableEntity().body("No UserId");
        } else if (credentials.battleId == null) {
            return ResponseEntity.unprocessableEntity().body("No BattleId");
        }

        // Check if the user is in a battle
        if(!userBattleComponent.getUserMap().containsKey(credentials.userId)) {
            return ResponseEntity.badRequest().body("User not in a battle");
        }
        // Get the battle info
        UserBattleInfo userBattleInfo = userBattleComponent.getUserMap().get(credentials.userId);

        // Validate the battleId
        if(!Objects.equals(userBattleInfo.battleId, credentials.battleId)){
            return ResponseEntity.badRequest().body("Wrong BattleID");
        }


        switch (userBattleInfo.turnInfoPackage.state){
            case New: // The battle has just started nothing to do
                return ResponseEntity.ok("TIP: New");
            case Ready: // The TIP is ready for simulation
                // Check that no team has dead monsters
//                for(int i = 0; i < userBattleInfo.turnInfoPackage.teamCount; i++){
//                    int teamIndex = userBattleInfo.turnInfoPackage.monInTeam * i;
//                    if(userBattleInfo.turnInfoPackage.hasDeadActive(userBattleInfo.turnInfoPackage.convertIntToSlot(teamIndex))){
////                       return ResponseEntity.accepted().body("TIP: Paused"); // Waiting for monster swap //TODO add per team checks
//                    }
//                } // Now we are not supposed to be paused
                // NOPE but this state can only be reached if the swap moves are sent

                //Set the state to simulating

                TurnInfoPackage tip = userBattleInfo.turnInfoPackage;
                userBattleInfo.turnInfoPackage.state = Simulating;
                //Begin Simulating
                apiService.callSimAPI(tip)
                    // When the simulation is complete
                    .subscribe(resultingTIP -> {
//                        System.out.println("Simulation Complete");
                        // Copy the UBI
                        UserBattleInfo newUBI = new UserBattleInfo(userBattleInfo.battleId, userBattleInfo.rules);
                        // Build out the new UBI

                        // Rest the move count for sending new moves
                        newUBI.moveCount = 0;
                        newUBI.lastBattleAction = LocalTime.now();
                        newUBI.turnInfoPackage = resultingTIP;
                        newUBI.rules = userBattleInfo.rules;
                        newUBI.turnLog = userBattleInfo.turnLog;

                        // Add to the display list to the log
                        newUBI.turnLog.addAll(resultingTIP.getTurnDisplayList().displayList);

                        // If this is the TIP state is complete then the turn is over,
                            // so add a new turn display element for the end of the turn
                        if(resultingTIP.state == Complete){
                            newUBI.turnLog.add(ConstantsConfig.createTurnStartDisplayElement(resultingTIP.turnCount));
                        }

                        // Replace the UserBattleInfo in the map
                        userBattleComponent.getUserMap().replace(credentials.userId, newUBI);

                    }, error -> {
                        // Handle any errors
                        System.err.println("Error occurred: (Simulation) " + error.getMessage());
                    });
                //Let the user know that the simulation is running
                return ResponseEntity.ok("TIP: Simulating");
            case Waiting: // Waiting for moves from the Ai
                return ResponseEntity.ok().body("TIP: Waiting");
            case Simulating: // The Simulation is currently running
                return ResponseEntity.ok("TIP: Simulating");
            case Resume:
                // Set up the TIP for the new turn to begin
                userBattleInfo.turnInfoPackage.state = Ready;
                userBattleInfo.turnInfoPackage.setTurnDisplayList(new TurnDisplayList());
                return ResponseEntity.ok("TIP: Resume");
            case Paused: // Paused because we need to make a monster swap
                // If the Ai has a dead monster (team of the last monster)
                // Check if the Ai has a dead monster
                boolean deadAi = false;
                for(int index = 0; index < userBattleInfo.rules.activeMon; index++) {
                    if (userBattleInfo.turnInfoPackage.getMonsters()[index + userBattleInfo.turnInfoPackage.monInTeam].isDead){
//                        System.out.println("Ai has a dead monster");
                        deadAi = true;
                    }
                }
                if (deadAi) {
                    // Check that the Ai has an alive monster to swap out
                    boolean aiHasAlive = false;
                    for(int index = 0; index < userBattleInfo.rules.activeMon; index++) {
                        if (!userBattleInfo.turnInfoPackage.getMonsters()[index + userBattleInfo.turnInfoPackage.monInTeam].isDead){
                            aiHasAlive = true;
                        }
                    }
                    if(aiHasAlive){
                        // If the Ai has an alive monster then they need to swap
                        // Get the Ai to make a monster swap choice
    //                    System.out.println("Ai is choosing swaps"); // Debugging
                        userBattleInfo.turnInfoPackage.state = Waiting;
                        aiService.getMonsterSwap(userBattleInfo.battleId)
                                .subscribe(aiSwaps -> {
                                    // Get the TIP
                                    TurnInfoPackage tip1 = userBattleInfo.turnInfoPackage;
                                    // Set the moveQueueBuilder
                                    MoveQueueBuilder aiMoveQueueBuilder = new MoveQueueBuilder();
                                    aiMoveQueueBuilder.moveQueue = tip1.getMoveQueue();
                                    // Push Moves to the Queue
                                    for (MoveEffect aiSwap : aiSwaps) {
                                        aiMoveQueueBuilder.pushMove(aiSwap, tip1);
                                    }
                                    userBattleInfo.turnInfoPackage.moveQueue = aiMoveQueueBuilder.moveQueue;
    //                                System.out.println("Ai Chosen swaps"); // Debugging

                                    // Set the state to resume
                                    userBattleInfo.turnInfoPackage.state = Resume;
                                }, error -> {
                                    // Handle any errors
                                    System.err.println("Error occurred: (Ai Swap) " + error.getMessage());
                                });
                        // Send The response to the user while the Ai is processing
                        // If the User team is dead let them know
                    } else {
                        // If the AI has no alive monsters then the game must go on without the AI making a move
                        userBattleInfo.turnInfoPackage.state = Resume;
                    }
                }

                // If the user has a dead monster tell them
                if(userBattleInfo.turnInfoPackage.hasDeadActive(userBattleInfo.turnInfoPackage.convertIntToSlot(0))){
                    userBattleInfo.turnInfoPackage.state = Paused; // Just in case the Ai has all dead Teammates
                    return ResponseEntity.accepted().body("TIP: Paused (Self)");
                }

                // If the AI was not dead then at this point the

                // Therefore only the Ai has a dead monster
                return ResponseEntity.accepted().body("TIP: Paused");
                //TODO add handling for monster swap if an AI monster dies
            case Complete: // The Simulation Has Completed and the TIP is ready for the user
                return ResponseEntity.ok("TIP: Complete");
            case End: // The Battle has ended
                return ResponseEntity.ok("TIP: End");
            default:
                return ResponseEntity.internalServerError().body("An error occurred. TurnInfoPackage State not handled");
        }
    }

    @PostMapping("/ping")
    public ResponseEntity<String> ping(@RequestBody PingCredentials credentials){
        // Validate the credentials
        if(credentials == null){
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        } else if(credentials.userId == null || credentials.userId.isEmpty()){
            return ResponseEntity.unprocessableEntity().body("No UserId");
        } else if ( credentials.battleId == null || credentials.battleId.isEmpty()){
            return ResponseEntity.unprocessableEntity().body("No BattleId");
        }
        // Check if the user is in a battle
        if(!userBattleComponent.getUserMap().containsKey(credentials.userId)){
            return ResponseEntity.accepted().body("User not in a battle");
        }

        UserBattleInfo userBattleInfo = userBattleComponent.getUserMap().get(credentials.userId);
        if (!Objects.equals(userBattleInfo.battleId, credentials.battleId)){
            return ResponseEntity.badRequest().body("Wrong BattleID");
        }

        updateLastAction(credentials.userId);
        return ResponseEntity.ok("received");
    }

    private String generateNewBattleKey(){
        String battleKey;
        do {
            battleKey = UUID.randomUUID().toString();
            boolean uniqueKey = true;
            for (Map.Entry<String, UserBattleInfo> entry : userBattleComponent.getUserMap().entrySet()) {
                UserBattleInfo userBattleInfo = entry.getValue();
                if (Objects.equals(userBattleInfo.battleId, battleKey)) {
                    uniqueKey = false;
                    break;
                }
            }
            if (uniqueKey) {
                return battleKey;
            }
        } while (true);
    }

    private void updateLastAction(String userId){
        if(userId == null){
            throw new IllegalArgumentException("No UserId"); // We throw an error so that I can get the stack trace
        }
        if (userBattleComponent.getUserMap().containsKey(userId)) {
            userBattleComponent.getUserMap().get(userId).lastBattleAction = LocalTime.now();
        } else {
            System.err.println("User not in the battle map"); // Just in case, might want to update logging
        }
    }


    private Monster monsterFromDexId(int dexId) throws NoSuchElementException{
        // Get the monster Entity
        MonsterEntity monsterEntity = monsterService.getMonsterById(dexId);
        // If the get fails throw error
        if(monsterEntity == null)
            throw new NoSuchElementException();
        // Build out the move list
        List<MoveEffect> movesList = new ArrayList<>();
        for(int moveId : monsterEntity.getMoveIds()){
            movesList.add(moveService.getMoveEffect(moveId));
        }
        // Add the swap move
        movesList.add(moveService.getMoveEffect(0));
        // Convert back to array
        MoveEffect[] moves = movesList.toArray(new MoveEffect[4]);
        // Build and send the monster
        return monsterEntity.toMonster(level,moves);
    }

    @PostMapping("/removeBattle")
    public ResponseEntity<String> removeBattle(@RequestBody BattleCredentials credentials){
        if (credentials == null){
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        } else if (credentials.userId == null){
            return ResponseEntity.unprocessableEntity().body("No UserId");
        } else if (credentials.battleId == null){
            return ResponseEntity.unprocessableEntity().body("No BattleId");
        }

        if(!userBattleComponent.getUserMap().containsKey(credentials.userId)){
            return ResponseEntity.accepted().body("User not in a battle");
        }

        userBattleComponent.getUserMap().remove(credentials.userId);
        if(aiService.endBattle(credentials.battleId)){
            return ResponseEntity.ok("Battle Removed");
        } else {
            return ResponseEntity.accepted().body("Battle Ended but Ai could not end");
        }
    }

    @PostMapping("/getLog")
    public ResponseEntity<String> getLog(@RequestBody BattleCredentials credentials){
        // Validate the credentials
        if (credentials == null){
            return ResponseEntity.unprocessableEntity().body("No Credentials");
        } else if (credentials.userId == null){
            return ResponseEntity.unprocessableEntity().body("No UserId");
        } else if (credentials.battleId == null){
            return ResponseEntity.unprocessableEntity().body("No BattleId");
        }

        // Check if the user is in a battle
        if(!userBattleComponent.getUserMap().containsKey(credentials.userId)){
            return ResponseEntity.accepted().body("User not in a battle");
        }

        UserBattleInfo userBattleInfo = userBattleComponent.getUserMap().get(credentials.userId);
        // Ensure the user has a log running
        if(userBattleInfo.turnLog == null){
            // This should not happen as the log is created when the battle is created
            return ResponseEntity.accepted().body("No Log");
        }
        // Return the log

        return ResponseEntity.ok(userBattleInfo.getTurnLogJson());
    }
}
