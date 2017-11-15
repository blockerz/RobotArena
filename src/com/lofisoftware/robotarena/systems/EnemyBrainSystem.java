package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.lofisoftware.robotarena.commands.Command;
import com.lofisoftware.robotarena.components.CommandComponent;
import com.lofisoftware.robotarena.components.EnemyBrainComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.TurnComponent.TURN;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.TurnCompleteMessage;

public class EnemyBrainSystem extends IteratingSystem  {

	float deltaTime;
	
	@SuppressWarnings("unchecked")
	public EnemyBrainSystem() {
		super(Family.getFor(EnemyBrainComponent.class, TurnComponent.class));
		deltaTime = 0f;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		this.deltaTime += deltaTime;
				
		if(Mappers.turnComponent.get(entity).state == TURN.GO && Mappers.stateComponent.get(entity).get() == STATE.READY) {
			
			Mappers.brainComponent.get(entity).enemyBrain.update(this.deltaTime);
			this.deltaTime = 0;
			//Mappers.turnComponent.get(entity).state = TURN.WAITING;
			MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE, new TurnCompleteMessage(entity));
		}
			
	}

}

