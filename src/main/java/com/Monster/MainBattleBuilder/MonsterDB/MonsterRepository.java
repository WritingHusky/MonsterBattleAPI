package com.Monster.MainBattleBuilder.MonsterDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonsterRepository extends JpaRepository<MonsterEntity, Integer> {
}
