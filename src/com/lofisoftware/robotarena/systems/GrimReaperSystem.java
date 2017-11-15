package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.lofisoftware.robotarena.components.BoundsComponent;
import com.lofisoftware.robotarena.components.CharacterComponent;
import com.lofisoftware.robotarena.components.RemoveEntityComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.StatsComponent;
import com.lofisoftware.robotarena.components.TextureComponent;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.TurnComponent.TURN;
import com.lofisoftware.robotarena.messages.DiedMessage;
import com.lofisoftware.robotarena.messages.EntityDamagedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.TurnCompleteMessage;

public class GrimReaperSystem implements Telegraph{

	PooledEngine engine; 
	
	public GrimReaperSystem(PooledEngine engine) {
		this.engine = engine;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.ENTITY_DAMAGED) {
			if(msg.extraInfo != null && EntityDamagedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity damaged = ((EntityDamagedMessage)msg.extraInfo).victim;
				Entity attacker = ((EntityDamagedMessage)msg.extraInfo).attacker;

				StatsComponent statsComponent = Mappers.statsComponent.get(damaged);
				CharacterComponent characterComponent = Mappers.characterComponent.get(damaged);
				//StateComponent stateComponent = Mappers.stateComponent.get(damaged);
				
				//Gdx.app.log("GrimReaperSystem:", characterComponent.getName() + " has health:" + statsComponent.getCurrentHealth());
				
				if (statsComponent.getCurrentHealth() <= 0) {
				
					Gdx.app.log("GrimReaperSystem:", characterComponent.getName() + " has died! :(");
					
					MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_DIED, new DiedMessage(damaged, attacker));
					
					if (Mappers.stateComponent.get(damaged) != null)
						Mappers.stateComponent.get(damaged).set(STATE.DEAD);
					
					if (Mappers.turnComponent.get(damaged)!=null && Mappers.turnComponent.get(damaged).state == TURN.GO) {
						Mappers.turnComponent.get(damaged).active = false;
						MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE, new TurnCompleteMessage(damaged));
					}
					
					damaged.remove(TurnComponent.class);
					damaged.remove(BoundsComponent.class);
					
					if (!Mappers.competitorComponent.has(damaged)) {
						
						damaged.remove(TextureComponent.class);
						
						damaged.add(new RemoveEntityComponent());
					}
					else {
						if (Mappers.cameraComponent.get(engine.getSystem(CameraSystem.class).getCamera()).target.getId() == damaged.getId()) {
							
							Mappers.textureComponent.get(damaged).tint = true;
							Mappers.textureComponent.get(damaged).tintColor = Color.LIGHT_GRAY;
							
							ImmutableArray<Entity> competitorEntities = engine.getSystem(CompetitorSystem.class).getCompetitiors(); 
							
							for (int e = 0; e < competitorEntities.size(); e++) {
								Entity entity = competitorEntities.get(e);
								if (Mappers.boundsComponent.has(entity) && Mappers.statsComponent.get(entity).getCurrentHealth() > 0) {
									Mappers.cameraComponent.get(engine.getSystem(CameraSystem.class).getCamera()).target = entity;
								}
							}
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	
}
