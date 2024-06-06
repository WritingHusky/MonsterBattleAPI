package com.Monster.MainBattleBuilder.MoveDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EffectRepository extends JpaRepository<EffectEntity, Integer> {
}
