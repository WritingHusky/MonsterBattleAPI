package com.Monster.MainBattleBuilder.MoveDB;

import org.MonsterBattler.Effect;
import org.MonsterBattler.MoveEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MoveService {
    @Autowired
    MoveRepository moveRepository;

    @Autowired
    EffectRepository effectRepository;

    @Autowired
    InteractionRepository interactionRepository;

    public MoveEffect getMoveEffect(int moveId){
        // Check that the move exists
        if (!moveRepository.existsById(moveId))
            return null;

        Optional<MoveEntity> moveEntityOptional = moveRepository.findById(moveId);
        if (moveEntityOptional.isEmpty())
            return null;
        MoveEntity moveEntity = moveEntityOptional.get();

        //Build out the moveEffect Queue
        Queue<Effect> effectQueue = new LinkedList<>();
        for (EffectEntity effectEntity: findEffectsByMoveId(moveId)){
            effectQueue.add(effectEntity.toEffect(moveEntity.typing));
        }
        return moveEntity.toMove(effectQueue);
    }

    public List<EffectEntity> findEffectsByMoveId(int moveId) {
        List<InteractionEntity> interactions = interactionRepository.findByMoveId(moveId);
        List<EffectEntity> effects = new ArrayList<>();
        for (InteractionEntity interaction : interactions) {
            EffectEntity effect = effectRepository.findById(interaction.effectId).orElse(null);
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects;
    }
}
