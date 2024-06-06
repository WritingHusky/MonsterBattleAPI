package com.Monster.MainBattleBuilder.Ai.Teams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiTeamRepository extends JpaRepository<AITeamEntity, String> {

}
