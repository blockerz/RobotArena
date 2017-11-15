package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lofisoftware.robotarena.components.StateComponent;


public class StateSystem extends IteratingSystem {	
	
	@SuppressWarnings("unchecked")
	public StateSystem() {
		super(Family.getFor(StateComponent.class));

	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Mappers.stateComponent.get(entity).time += deltaTime;
	}
}
