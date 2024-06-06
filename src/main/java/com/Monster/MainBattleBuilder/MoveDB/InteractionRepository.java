package com.Monster.MainBattleBuilder.MoveDB;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionRepository extends JpaRepository<InteractionEntity,Integer> {
    List<InteractionEntity> findByMoveId(int moveId);
}
