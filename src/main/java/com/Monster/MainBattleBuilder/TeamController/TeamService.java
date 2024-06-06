package com.Monster.MainBattleBuilder.TeamController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public void addTeam(TeamEntity team){
        teamRepository.save(team);
    }
    public void addTeam(String teamId){
        TeamEntity team = new TeamEntity(teamId);
        teamRepository.save(team);
    }

    public Optional<TeamEntity> readTeam(String teamId){
        return teamRepository.findById(teamId);
    }

    public boolean isTeamInTable(String teamId){
        return teamRepository.existsById(teamId);
    }

    public void removeTeam(String teamId){
        if(isTeamInTable(teamId)){
            teamRepository.deleteById(teamId);
        }
    }

}
