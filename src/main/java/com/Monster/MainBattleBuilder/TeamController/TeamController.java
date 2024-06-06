package com.Monster.MainBattleBuilder.TeamController;

import com.Monster.MainBattleBuilder.ConstantsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = ConstantsConfig.webUrl)
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * A way to add a new row into the teams table.
     * @param credentials The Credentials of the given user that would like to add a new team
     * @return The status of the creation (created, max reached, already full, failed, etc...)
     */
    @PostMapping("/create")
    public ResponseEntity<String> createTeam(@RequestBody TeamCredentials credentials){
        //Validate user
        if(Objects.equals(credentials.username, "")){
            return ResponseEntity.badRequest().body("Cannot have blank username");
        }

        // TODO check user is signed in (When heartbeat is implemented)

        //Check if user already has a team
        if (teamService.isTeamInTable(credentials.username)){
            return ConstantsConfig.UnAuthorizedError;
        }
        //Add the Team To the table
        teamService.addTeam(credentials.username);

        //Retrieve the team data to send back
        if(teamService.readTeam(credentials.username).isPresent()) {
            return ResponseEntity.ok().body(teamService.readTeam(credentials.username).get().toJson());
        } else {
            return ResponseEntity.internalServerError().body("Team is in table but was not read");
        }
    }

    /**
     * A way to get the team information for a user
     * @param credentials The credentials of the user that would like to get the information
     * @return The teams that the user has. If there are no teams (TODO figure out what happens here)
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestTeam(@RequestBody TeamCredentials credentials){
        //Validate the user
        if(Objects.equals(credentials.username, "" )|| credentials.username == null){
            return ResponseEntity.badRequest().body("Cannot have blank username");
        }

        //Check if the team is in the table
        if(!teamService.isTeamInTable(credentials.username)){
            return ConstantsConfig.UnAuthorizedError;
        }
        //Retrieve the team data
        if(teamService.readTeam(credentials.username).isPresent()) {
            return ResponseEntity.ok().body(teamService.readTeam(credentials.username).get().toJson());
        } else {
            return ResponseEntity.internalServerError().body("Team is in table but was not read");
        }
    }

    /**
     * This is the way to save the team into the team db.<br>
     * There will some validation of the team before saving
     * @param credentials The credentials of the user that would like to update their team
     * @return The confirmation status of the update request (valid, invalid, failed, etc...)
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateTeam(@RequestBody FullTeamCredentials credentials){
        //Validate the user

        //Check if there is a team in the table
        if(!teamService.isTeamInTable(credentials.username)){
            return ResponseEntity.badRequest().body("Team is not in table");
        }
        //Save the team data
        teamService.addTeam(credentials.username);
        return ResponseEntity.ok("Team Saved");
    }

    /**
     * The way to remove teams from the team db
     * @param credentials The credentials of the user that wants to delete the team
     * @return The Confirmation of the deletion
     */
    @PostMapping("/delete")
    public ResponseEntity<String> deleteTeam(@RequestBody TeamCredentials credentials){
        //Validate the user
        if (!teamService.isTeamInTable(credentials.username)){
            return ResponseEntity.accepted().body("Team Does not exist already");
        }
        //Remove the user
        teamService.removeTeam(credentials.username);
        return ResponseEntity.ok("Team Removed");
    }

    @GetMapping("/ping")
    public ResponseEntity<String> isTeam(@RequestBody String teamId){
        if(teamService.isTeamInTable(teamId)){
            return ResponseEntity.ok("Is In");
        } else {
            return ConstantsConfig.UnAuthorizedError;
        }
    }
}
