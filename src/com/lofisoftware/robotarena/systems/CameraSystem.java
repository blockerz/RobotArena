package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.lofisoftware.robotarena.components.CameraComponent;
import com.lofisoftware.robotarena.components.CompetitorComponent;
import com.lofisoftware.robotarena.components.TransformComponent;


public class CameraSystem extends IteratingSystem {

	ImmutableArray<Entity> cameraEntities;
	
	@SuppressWarnings("unchecked")
	public CameraSystem() {
		super(Family.getFor(CameraComponent.class));

	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CameraComponent cam = Mappers.cameraComponent.get(entity);
		
		if (cam.target == null) {
			return;
		}
		
		TransformComponent target = Mappers.transformComponent.get(cam.target);
		
		if (target == null) {
			return;
		}
		
		cam.camera.position.x = target.pos.x + 0.5f + cam.offsetX;
		cam.camera.position.y = target.pos.y + 0.5f + cam.offsetY;
		//cam.camera.position.x = Math.max(cam.camera.position.x, target.pos.x);
		//cam.camera.position.y = Math.max(cam.camera.position.y, target.pos.y);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		cameraEntities = engine.getEntitiesFor(Family.getFor(CameraComponent.class));
		

	}
	
	public Entity getCamera() {
		if (cameraEntities.size() > 0)
			return cameraEntities.first();
		return null;
	}
	
}
