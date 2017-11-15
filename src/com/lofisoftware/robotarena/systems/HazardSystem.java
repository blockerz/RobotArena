package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.lofisoftware.robotarena.components.HazardComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.messages.Messages;

public class HazardSystem extends IteratingSystem implements Telegraph {

	private ImmutableArray<Entity> hazardousEntities;
	
	@SuppressWarnings("unchecked")
	public HazardSystem() {
		super(Family.getFor(HazardComponent.class));
		
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {

/*		if (Mappers.hazardComponent.get(entity).activate) {
			Mappers.hazardComponent.get(entity).command.execute();
			Mappers.hazardComponent.get(entity).activate = false;
		}*/
	}
	
/*	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		

		//MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE);
		
	}*/
	
	@Override
	public boolean handleMessage(Telegram msg) {
		
		if (msg.message == Messages.ROUND_COMPLETE && hazardousEntities != null) {
			for (int e = 0; e < hazardousEntities.size(); e++) {
				Entity entity = hazardousEntities.get(e);
				Mappers.hazardComponent.get(entity).activate = true;			
				Mappers.hazardComponent.get(entity).command.execute();
				Mappers.hazardComponent.get(entity).activate = false;
			}
		}
		return false;
	}
	
	public Entity getHazardsAt(int x, int y, long zoneID) {
		Array<Entity> entities = getHazardsAt(new Rectangle(x,y,1,1), zoneID);
		if (entities.size >= 1)
			return entities.get(0);
		return null;
	}
	
	public Array<Entity> getHazardsAt(Rectangle bounds, long zoneID) {
		Array<Entity> entities = new Array<Entity>();
		for (int i = 0; i < hazardousEntities.size(); ++i) {
			Entity hazardEntity = hazardousEntities.get(i);
			TransformComponent position = Mappers.transformComponent.get(hazardEntity);
			
			if (zoneID != position.zoneID || entities.contains(hazardEntity, true))
				continue;
			
			if (bounds.contains(MathUtils.floor(position.newPos.x), MathUtils.floor(position.newPos.y))) {				
				Gdx.app.log("HazardSystem:getEntitiesAt"," Hazard Found: " + bounds.toString());

				entities.add(hazardEntity);
			}	 
		}
		return entities;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		hazardousEntities = engine.getEntitiesFor(Family.getFor(HazardComponent.class));
		

	}
}