package com.Monster.MainBattleBuilder.MonsterDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonsterService {

    @Autowired
    private MonsterRepository monsterRepository;

    public boolean addMonster(MonsterEntity monster){
        if(monsterRepository.existsById(monster.dexId)){
            return false;
        }
        monsterRepository.save(monster);
        return true;
    }
    public boolean hasMonster(int dexId){
        return monsterRepository.existsById(dexId);
    }

    public int count(){
        return (int) monsterRepository.count();
    }

    public MonsterEntity getMonsterById(int dexId){
        if (monsterRepository.findById(dexId).isEmpty()){
            return null;
        }
        return monsterRepository.findById(dexId).get();
    }
}
