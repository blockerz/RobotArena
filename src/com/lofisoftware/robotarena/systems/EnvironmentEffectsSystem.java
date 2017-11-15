package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.lofisoftware.robotarena.commands.Command;
import com.lofisoftware.robotarena.commands.MoveCommand;
import com.lofisoftware.robotarena.messages.EntityPushedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.PushMessage;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;

public class EnvironmentEffectsSystem implements Telegraph {

	PooledEngine engine;
	
	public EnvironmentEffectsSystem (PooledEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		
		if (msg.message == Messages.PUSH) {
			if(msg.extraInfo != null && PushMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity pusher = ((PushMessage)msg.extraInfo).pusher;
				Entity pushee = ((PushMessage)msg.extraInfo).pushee;
				Direction direction = ((PushMessage)msg.extraInfo).direction;

				if (canPush(pushee, direction)) {
					pushEntity(pusher, pushee,direction);
				}
			}
		}
		
		return false;
	}

	
	private void pushEntity(Entity pusher, Entity pushee, Direction direction) {
		
		Command command = new MoveCommand(engine,pushee,direction);
		command.execute();
		//Point mxmy = Direction.getMxMy(direction);
		
		//TransformComponent transform = Mappers.transformComponent.get(pushee);

		MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_PUSHED, new EntityPushedMessage(pusher, pushee));
		
	}


	private boolean canPush(Entity pushee, Direction direction) {
		if (Mappers.transformComponent.has(pushee) && Mappers.stateComponent.has(pushee) && Mappers.characterComponent.has(pushee)) {
			
			Point pushPosition = new Point(Mappers.transformComponent.get(pushee).newPos.x, Mappers.transformComponent.get(pushee).newPos.y).plus(Direction.getMxMy(direction));
			
			if (Mappers.zoneComponent.get(engine.getSystem(ZoneSystem.class).getActiveZone()).zone.isPassable(pushPosition.x(), pushPosition.y())
					&& engine.getSystem(CollisionSystem.class).getEntityAt(pushPosition.x(), pushPosition.y(), Mappers.transformComponent.get(pushee).zoneID) == null)
				return true;
		}
		return false;
	}
}