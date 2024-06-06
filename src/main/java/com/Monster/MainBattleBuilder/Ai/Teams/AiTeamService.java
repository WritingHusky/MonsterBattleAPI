package com.Monster.MainBattleBuilder.Ai.Teams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiTeamService {

    @Autowired
    private AiTeamRepository aiTeamRepository;

    public void addTeam(AITeamEntity team){
        aiTeamRepository.save(team);
    }
    public void addTeam(String teamId){
        AITeamEntity team = new AITeamEntity(teamId);
        aiTeamRepository.save(team);
    }

    public Optional<AITeamEntity> readTeam(String teamId){
        return aiTeamRepository.findById(teamId);
    }

    public boolean isTeamInTable(String teamId){
        return aiTeamRepository.existsById(teamId);
    }

    public void removeTeam(String teamId){
        if(isTeamInTable(teamId)){
            aiTeamRepository.deleteById(teamId);
        }
    }

}
