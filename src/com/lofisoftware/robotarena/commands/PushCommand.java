package com.lofisoftware.robotarena.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.PushMessage;
import com.lofisoftware.robotarena.systems.CollisionSystem;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.world.Direction;

public class PushCommand implements Command {
	
	Direction direction;
	Entity entity;
	PooledEngine engine;
	
	public PushCommand (PooledEngine engine, Entity entity, Direction direction) {
		
		this.direction = direction;
		this.entity = entity;
		this.engine = engine;
	}
	
	@Override
	public void execute() {

		//Point mxmy = Direction.getMxMy(direction);
		
		Vector3 location = Mappers.transformComponent.get(entity).newPos;
		Entity victim = engine.getSystem(CollisionSystem.class).getEntityAt(MathUtils.floor(location.x), MathUtils.floor(location.y), Mappers.transformComponent.get(entity).zoneID);
		if (victim != null)
			MessageDispatcher.getInstance().dispatchMessage(null, Messages.PUSH, new PushMessage(entity, victim, direction));
		
	}

}