package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.lofisoftware.robotarena.GameEngine;
import com.lofisoftware.robotarena.components.ActiveZoneComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;
import com.lofisoftware.robotarena.messages.EntityMovedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.ZoneChangeMessage;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.ZoneConnection;

public class ZoneSystem extends IteratingSystem implements Telegraph{	
	
	//Entity active;
	//Entity last;
	//Entity player;
	GameEngine gameEngine;
	boolean playerMoved;
	ImmutableArray<Entity> activeZoneEntities;
	
	@SuppressWarnings("unchecked")
	public ZoneSystem(GameEngine gameEngine) {
		super(Family.getFor(ZoneComponent.class, ActiveZoneComponent.class));
		this.gameEngine = gameEngine;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		//StateComponent heroState = Mappers.stateComponent.get(gameEngine.getHero());
		
		//if (entityMoved && heroState.get() == STATE.READY) {
		if (playerMoved) {
			//ActiveZoneComponent activeZoneComponent = Mappers.activeZoneComponent.get(entity);
			ZoneComponent zoneComponent = Mappers.zoneComponent.get(entity);
			
			TransformComponent transComponent = Mappers.transformComponent.get(gameEngine.getHero());
			Point position = new Point(MathUtils.floor(transComponent.newPos.x),MathUtils.floor(transComponent.newPos.y));
			
			if (zoneComponent.connections.containsKey(position)) {
				//Gdx.app.log("ZoneSystem:processEntity"," Change ActiveZone :" + transComponent.newPos.toString());
				
				ZoneConnection newZone = zoneComponent.connections.get(position);
				
				//transComponent.zoneID = newZone.getId();
				
				MessageDispatcher.getInstance().dispatchMessage(null, Messages.ZONE_CHANGE, new ZoneChangeMessage(gameEngine.getHero(), entity,newZone.connectingZone, newZone.connectionPoint));
				
			}
			playerMoved = false;
		}
		//setActiveZone(entity);
	}
	
	/*
	public void setActiveZone(Entity newActiveZone) {
		
		if(active.getId() != newActiveZone.getId()) {
			last = active;
			active = newActiveZone;
			//last.remove(ActiveZoneComponent.class);
			//active.add(engine.createComponent(ActiveZoneComponent.class));
		}
	}
	
	public Entity getActiveZone() {
		return active;
	}
	*/
	
	public boolean createZoneConnection(Entity zone1, Point position1, Entity zone2, Point position2) {
		
		ZoneComponent zoneComponent1 = Mappers.zoneComponent.get(zone1);
		ZoneComponent zoneComponent2 = Mappers.zoneComponent.get(zone2);
		
		if (zoneComponent1 != null && zoneComponent2 != null) {
			if (zoneComponent1.zone.isPassable(position1.x(), position1.y())
					&& zoneComponent2.zone.isPassable(position2.x(), position2.y()) ){
				
				zoneComponent1.connections.put(position1,new ZoneConnection(zone2,position2,position1));
				zoneComponent2.connections.put(position2,new ZoneConnection(zone1,position1,position2));
				
				return true;
			}
			return false;
		}
		return false;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.ENTITY_MOVED) {
			if(msg.extraInfo != null && EntityMovedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity moved = ((EntityMovedMessage)msg.extraInfo).moved;
				if (moved.getId() == gameEngine.getHero().getId()) {
					//Mappers.fieldOfViewComponent.get(moved).update = true;
					playerMoved = true;
					return true;
				}
			}
		}

		return false;
	}
	
	public Entity getActiveZone () {
		if (activeZoneEntities.size() > 0)
			return activeZoneEntities.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		activeZoneEntities = engine.getEntitiesFor(Family.getFor(ZoneComponent.class, ActiveZoneComponent.class));
		

	}
}
