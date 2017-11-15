package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.lofisoftware.robotarena.GameEngine;
import com.lofisoftware.robotarena.components.BoundsComponent;
import com.lofisoftware.robotarena.components.ColorComponent;
import com.lofisoftware.robotarena.components.MovementComponent;
import com.lofisoftware.robotarena.components.StateComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.messages.CollisionMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.world.Direction;

public class CollisionSystem extends IteratingSystem {
	
	private GameEngine gameEngine;
	private ImmutableArray<Entity> collidableEntities;
	
	//PerformanceCounter counter;
	
	@SuppressWarnings("unchecked")
	public CollisionSystem(GameEngine gameEngine) {
		super(Family.getFor(BoundsComponent.class, TransformComponent.class, StateComponent.class, MovementComponent.class));
		this.gameEngine = gameEngine;
		//counter = new PerformanceCounter("Collision System");
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		
		//counter.tick(deltaTime);
		//counter.start();
		
		BoundsComponent bounds = Mappers.boundsComponent.get(entity);
		MovementComponent mov = Mappers.movementComponent.get(entity);
		TransformComponent pos = Mappers.transformComponent.get(entity);
		
		Vector3 moveTo = new Vector3(mov.move.x + pos.newPos.x,mov.move.y + pos.newPos.y,mov.move.z + pos.newPos.z);
		Rectangle newBounds = new Rectangle(moveTo.x, moveTo.y,bounds.bounds.width,bounds.bounds.height); 
		
		//Gdx.app.log("CollisionSystem:"," moveTo: " + moveTo.toString());
		
		if (!gameEngine.isPassable(MathUtils.floor(moveTo.x), MathUtils.floor(moveTo.y))) {
			
			entity.getComponent(StateComponent.class).set(STATE.MAP_COLLISION);
		
			addColorAnimation(entity, Color.YELLOW);
			
			//Gdx.app.log("CollisionSystem:processEntity"," Map Collision Found");
			//counter.stop();
			return;
		
		}
		
		//Gdx.app.log("CollisionSystem:processEntity"," collidableEntities size: " + collidableEntities.size());
		
		for (int i = 0; i < collidableEntities.size(); ++i) {
			Entity collidableEntity = collidableEntities.get(i);
			
			if (Mappers.transformComponent.get(entity).zoneID != Mappers.transformComponent.get(collidableEntity).zoneID			
					||  entity.getId() == collidableEntity.getId())
				continue;
			
			//MovementComponent collidableEntityMovement = mm.get(collidableEntity);
			BoundsComponent collidableEntityBounds = Mappers.boundsComponent.get(collidableEntity);
			//TransformComponent collidableEntityTransform = tm.get(collidableEntity);
			
			//Gdx.app.log("CollisionSystem:"," collidableEntityBounds: " + collidableEntityBounds.bounds.toString());
			//Gdx.app.log("CollisionSystem:"," newBounds: " + newBounds.toString());
			//Gdx.app.log("CollisionSystem:"," collidable: " + collidableEntity.getId());
			//Gdx.app.log("CollisionSystem:"," entity: " + entity.getId());
			
			if (collidableEntityBounds.bounds.overlaps(newBounds)) {
				entity.getComponent(StateComponent.class).set(STATE.MAP_COLLISION);
				
				addColorAnimation(collidableEntity, Color.RED);
				
				MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_COLLISION, new CollisionMessage(entity,collidableEntity,Direction.getXYDirection(MathUtils.floor(mov.move.x), MathUtils.floor(mov.move.y))));
				//Gdx.app.log("CollisionSystem:update", "Counter load: " + counter.toString());
				//Gdx.app.log("CollisionSystem:processEntity"," Entity Collision Found");
			}
			
			 
		}
		//counter.stop();
			
	}
	
	public Entity addColorAnimation(Entity entity, Color color) {
		ColorComponent colorComponent = gameEngine.getEngine().createComponent(ColorComponent.class);
		colorComponent.time = 0.25f;
		colorComponent.tintColor = color;
		entity.add(colorComponent);
		
		return entity;
	}
	
	public Entity getEntityAt(int x, int y, long zoneID) {
		Array<Entity> entities = getEntitiesAt(new Rectangle(x,y,1,1), zoneID);
		if (entities.size >= 1)
			return entities.get(0);
		return null;
	}
	
	public Array<Entity> getEntitiesAt(Rectangle bounds, long zoneID) {
		Array<Entity> entities = new Array<Entity>();
		for (int i = 0; i < collidableEntities.size(); ++i) {
			Entity collidableEntity = collidableEntities.get(i);

			if (zoneID != Mappers.transformComponent.get(collidableEntity).zoneID
					|| entities.contains(collidableEntity, true))
				continue;
			
			BoundsComponent collidableEntityBounds = Mappers.boundsComponent.get(collidableEntity);
			
			if (collidableEntityBounds.bounds.overlaps(bounds)) {				
				//Gdx.app.log("CollisionSystem:getEntitiesAt"," Entity Collision Found: " + collidableEntityBounds.bounds.toString());

				entities.add(collidableEntity);
			}	 
		}
		return entities;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		// remove StateComponent to allow collisions with things that do not have State
		collidableEntities = engine.getEntitiesFor(Family.getFor(BoundsComponent.class, TransformComponent.class));

	}

}
