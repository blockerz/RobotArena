package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lofisoftware.robotarena.components.BoundsComponent;
import com.lofisoftware.robotarena.components.TransformComponent;


public class BoundsSystem extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	public BoundsSystem() {
		super(Family.getFor(BoundsComponent.class, TransformComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = Mappers.transformComponent.get(entity);
		BoundsComponent bounds = Mappers.boundsComponent.get(entity);
		
		bounds.bounds.x = pos.newPos.x;
		bounds.bounds.y = pos.newPos.y;
	}
}
