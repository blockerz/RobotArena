package com.lofisoftware.robotarena.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.components.ItemComponent;
import com.lofisoftware.robotarena.components.RemoveEntityComponent;
import com.lofisoftware.robotarena.components.TextureComponent;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.RepairMessage;
import com.lofisoftware.robotarena.systems.CollisionSystem;
import com.lofisoftware.robotarena.systems.Mappers;

public class RepairCommand implements Command {
	
	Entity entity;
	PooledEngine engine;
	
	public static final int REPAIR_AMOUNT = 1;
	
	public RepairCommand (PooledEngine engine, Entity entity) {
		
		this.entity = entity;
		this.engine = engine;
	}
	
	@Override
	public void execute() {

		//Point mxmy = Direction.getMxMy(direction);
		
		Vector3 location = Mappers.transformComponent.get(entity).newPos; 
		Entity patient = engine.getSystem(CollisionSystem.class).getEntityAt(MathUtils.floor(location.x), MathUtils.floor(location.y), Mappers.transformComponent.get(entity).zoneID);
		if (patient != null && Mappers.competitorComponent.has(patient)) {
			MessageDispatcher.getInstance().dispatchMessage(null, Messages.REPAIR, new RepairMessage(patient, Mappers.characterComponent.get(entity).getName(), REPAIR_AMOUNT));
		
			entity.remove(ItemComponent.class);
			entity.remove(TextureComponent.class);
			entity.add(new RemoveEntityComponent());
		}
		
	}

}