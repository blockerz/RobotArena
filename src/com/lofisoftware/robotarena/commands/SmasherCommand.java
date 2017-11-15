package com.lofisoftware.robotarena.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.messages.DamageMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.systems.CollisionSystem;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;

public class SmasherCommand implements Command {
	
	Direction direction;
	Entity smasher;
	Entity smasherBar;
	PooledEngine engine;
	
	public static final int SMASHER_DAMAGE = 1;
	
	public SmasherCommand (PooledEngine engine, Entity smasher, Entity smasherBar, Direction direction) {
		
		this.direction = direction;
		this.smasher = smasher;
		this.smasherBar = smasherBar;
		this.engine = engine;
	}
	
	@Override
	public void execute() {

		boolean extend = false;
		
		if (Mappers.transformComponent.get(smasher).newPos.x == Mappers.transformComponent.get(smasherBar).newPos.x 
				&& Mappers.transformComponent.get(smasher).newPos.y == Mappers.transformComponent.get(smasherBar).newPos.y)
			extend = true;
		
		Point mxmy = Direction.getMxMy((extend)?direction:Direction.opposite(direction));
		
		Mappers.transformComponent.get(smasher).newPos.x = Mappers.transformComponent.get(smasher).pos.x = MathUtils.floor(Mappers.transformComponent.get(smasher).newPos.x + mxmy.x());
		Mappers.transformComponent.get(smasher).newPos.y = Mappers.transformComponent.get(smasher).pos.y = MathUtils.floor(Mappers.transformComponent.get(smasher).newPos.y + mxmy.y());
		
		Vector3 location = Mappers.transformComponent.get(smasher).newPos;
		Entity victim = engine.getSystem(CollisionSystem.class).getEntityAt(MathUtils.floor(location.x), MathUtils.floor(location.y), Mappers.transformComponent.get(smasher).zoneID);
		if (victim != null && victim.getId() != smasherBar.getId())
			MessageDispatcher.getInstance().dispatchMessage(null, Messages.DAMAGE, new DamageMessage(victim, smasher, SMASHER_DAMAGE));
		
	}

}