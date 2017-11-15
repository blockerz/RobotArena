package com.lofisoftware.robotarena.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.lofisoftware.robotarena.components.MovementComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.messages.EntityMovedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;

public class MoveCommand implements Command {
	
	Direction direction;
	Entity entity;
	PooledEngine engine;
	
	public MoveCommand(PooledEngine engine, Entity entity, Direction direction) {
		
		this.direction = direction;
		this.entity = entity;
		this.engine = engine;
	}
	
	@Override
	public void execute() {

		Point mxmy;
		
		mxmy = Direction.getMxMy(direction);
		
		 
	    MovementComponent movement = Mappers.movementComponent.get(entity);
	    if (movement == null)
	    	movement = engine.createComponent(MovementComponent.class);
		movement.animate = true;
		movement.animationTime = 0.25f;
		//movement.move.set(deltaX,deltaY,0);
		movement.move.add(mxmy.x(),mxmy.y(),0);
		entity.add(movement);
		//Gdx.app.log("MoveCommand:execute"," I'm going to move " + direction.direction());
		Mappers.stateComponent.get(entity).set(STATE.MOVE_QUEUED);

		
	}

	public Direction getDirection() {
		return direction;
	}

}
