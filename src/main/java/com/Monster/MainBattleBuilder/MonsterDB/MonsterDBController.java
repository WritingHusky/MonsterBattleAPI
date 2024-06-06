package com.Monster.MainBattleBuilder.MonsterDB;

import com.Monster.MainBattleBuilder.ConstantsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/monster")
@CrossOrigin(origins = {ConstantsConfig.webUrl ,"http://localhost:8080"})
public class MonsterDBController {

    @Autowired
    private MonsterService monsterService;

    @PostMapping("/add")
    public ResponseEntity<String> addMonster(@RequestBody MonsterAddCredentials credentials){
        //Handle possible errors
        if(credentials == null){
            return ConstantsConfig.EmptyInputError;
        } if (Objects.equals(credentials.username, "") || Objects.equals(credentials.password, "")){
            return ConstantsConfig.EmptyInputError;
        } if (!(Objects.equals(credentials.username, "Ethan") && Objects.equals(credentials.password, "Hash"))){
            return ConstantsConfig.UnAuthorizedError;
        }

        monsterService.addMonster(credentials.monster);

        if(!monsterService.hasMonster(credentials.monster.dexId)){
            return ResponseEntity.internalServerError().body("Monster Entered But Not Found");
        }
        return ResponseEntity.ok("Received");
    }

    @PostMapping("/retrieve")
    public ResponseEntity<String> getMonster(@RequestBody MonRequest monRequest) {
        //Handle possible errors
        if (monRequest == null) {
            return ConstantsConfig.EmptyInputError;
        } else if( monRequest.dexId < 0 || monRequest.dexId > monsterService.count()){
            return ResponseEntity.unprocessableEntity().body("Invalid number");
        } else if (!monsterService.hasMonster(monRequest.dexId)){
            return ResponseEntity.status(404).body("Monster Is not in the database");
        }
        return  ResponseEntity.ok().body(monsterService.getMonsterById(monRequest.dexId).toJson());
    }

    @GetMapping("/count")
    public ResponseEntity<String> getNumberOfMons(){
        return ResponseEntity.ok().body(Integer.toString(monsterService.count()));
    }
}
