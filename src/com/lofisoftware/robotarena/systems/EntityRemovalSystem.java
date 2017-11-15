package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lofisoftware.robotarena.components.RemoveEntityComponent;

public class EntityRemovalSystem extends IteratingSystem {
	
	private PooledEngine engine;
	private final float REMOVAL_TIME = 3.0f;
	
	@SuppressWarnings("unchecked")
	public EntityRemovalSystem(PooledEngine gameEngine) {
		super(Family.getFor(RemoveEntityComponent.class),2);
		this.engine = gameEngine;

	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Mappers.removeEntityComponent.get(entity).time += deltaTime;
		
		if (Mappers.removeEntityComponent.get(entity).time >= REMOVAL_TIME)
			engine.removeEntity(entity);
		
	}
}
