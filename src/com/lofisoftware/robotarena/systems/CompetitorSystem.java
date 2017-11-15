package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.components.BoundsComponent;
import com.lofisoftware.robotarena.components.CompetitorComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.messages.FinishMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.TurnCompleteMessage;
import com.lofisoftware.robotarena.world.Tile;
import com.lofisoftware.robotarena.world.Zone;

public class CompetitorSystem extends IteratingSystem implements Telegraph {

	private ImmutableArray<Entity> competitorEntities;
	PooledEngine engine;
	
	@SuppressWarnings("unchecked")
	public CompetitorSystem(PooledEngine engine) {
		super(Family.getFor(CompetitorComponent.class));
		this.engine = engine;
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {

	}
	

	
	@Override
	public boolean handleMessage(Telegram msg) {
		
		if (msg.message == Messages.TURN_COMPLETE) {
			
			if(msg.extraInfo != null && TurnCompleteMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ) {
				Entity entity = ((TurnCompleteMessage)msg.extraInfo).entity;
				
				if (Mappers.competitorComponent.has(entity)) {
					TransformComponent transform = Mappers.transformComponent.get(entity);
					Entity zone = engine.getSystem(ZoneSystem.class).getActiveZone();
					
					if (Mappers.zoneComponent.get(zone).zone.getZoneTile(MathUtils.floor(transform.newPos.x), MathUtils.floor(transform.newPos.y), Zone.ZONE_LAYER.BASE.getLayer()) == Tile.FLOOR_SMALL_CHECK) {
						// player reached finish line
						
						int finishPlace = 0;
						
						for (int e = 0; e < competitorEntities.size(); e++) {
							Entity competitor = competitorEntities.get(e);
							int place = Mappers.competitorComponent.get(competitor).place;
							if (competitor.getId() != entity.getId()) {
								if (finishPlace <= place)
									finishPlace = place + 1;
							}		
						}
						
						entity.remove(TurnComponent.class);
						entity.remove(BoundsComponent.class);
						
						Mappers.competitorComponent.get(entity).place = finishPlace;
						MessageDispatcher.getInstance().dispatchMessage(null, Messages.FINISHED, new FinishMessage(entity, finishPlace));
						
						if (Mappers.cameraComponent.get(engine.getSystem(CameraSystem.class).getCamera()).target.getId() == entity.getId()) {
							//ImmutableArray<Entity> competitorEntities = engine.getSystem(CompetitorSystem.class).getCompetitiors(); 
							
							for (int e = 0; e < competitorEntities.size(); e++) {
								Entity competitor = competitorEntities.get(e);
								if (Mappers.boundsComponent.has(competitor) && Mappers.statsComponent.get(competitor).getCurrentHealth() > 0) {
									Mappers.cameraComponent.get(engine.getSystem(CameraSystem.class).getCamera()).target = entity;
								}
							}
						}
						
						if (finishPlace >= 4) {
							MessageDispatcher.getInstance().dispatchMessage(null, Messages.GAME_OVER);
						}
					}
				}					
				
			}

			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		competitorEntities = engine.getEntitiesFor(Family.getFor(CompetitorComponent.class));
		

	}
	

	public Entity getNearestCompetitorInView(Vector3 startPosition, float maxDistance, Entity self) {
		
		Entity nearest = null;
		
		float distance = 0, closest = 9999999;
		
		ImmutableArray<Entity> competitors = getCompetitiors();
		
		for (int e = 0; e < competitors.size(); e++) {
			Entity entity = competitors.get(e);
			
			if (self != null && self.getId() == entity.getId())
				continue;
			
			distance = Mappers.transformComponent.get(entity).newPos.dst(startPosition);
			
			if (distance < closest && distance <= maxDistance) {
				nearest = entity;
				closest = distance;
			}
		}
		
		return nearest;
		
	}
	
	public ImmutableArray<Entity> getCompetitiors() {
		/*			
		for (int e = 0; e < competitorEntities.size(); e++) {
			Entity entity = competitorEntities.get(e);
		}*/
		return competitorEntities;
	}
}