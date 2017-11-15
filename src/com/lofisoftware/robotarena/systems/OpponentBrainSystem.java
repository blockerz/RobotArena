package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.lofisoftware.robotarena.components.OpponentBrainComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.TurnComponent.TURN;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.TurnCompleteMessage;

public class OpponentBrainSystem extends IteratingSystem  {

	float deltaTime;
	
	@SuppressWarnings("unchecked")
	public OpponentBrainSystem() {
		super(Family.getFor(OpponentBrainComponent.class, TurnComponent.class));
		deltaTime = 0f;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		this.deltaTime += deltaTime;
				
		if(Mappers.turnComponent.get(entity).state == TURN.GO && Mappers.stateComponent.get(entity).get() == STATE.READY) {
			
			Mappers.opponentBrainComponent.get(entity).opponentBrain.update(this.deltaTime);
			this.deltaTime = 0;
			//Mappers.turnComponent.get(entity).state = TURN.WAITING;
			MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE, new TurnCompleteMessage(entity));
		}
			
	}

}