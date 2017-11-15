package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.lofisoftware.robotarena.components.ActiveZoneComponent;
import com.lofisoftware.robotarena.components.FieldOfViewComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;
import com.lofisoftware.robotarena.messages.EntityMovedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.ZoneChangeMessage;
import com.lofisoftware.robotarena.util.Line;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.FieldOfView;

public class FieldOfViewSystem extends IteratingSystem implements Telegraph{

	private ImmutableArray<Entity> activeZone;
	private PooledEngine engine;
	private boolean updateFOV;
	
	@SuppressWarnings("unchecked")
	public FieldOfViewSystem(PooledEngine engine) {
		super(Family.getFor(TransformComponent.class, FieldOfViewComponent.class));
		
		this.engine = engine;
		updateFOV = false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processEntity(Entity entity, float deltaTime) {

		TransformComponent pos = Mappers.transformComponent.get(entity);
		FieldOfViewComponent fov = Mappers.fieldOfViewComponent.get(entity);
			//HeroComponent hero = Mappers.heroComponent.get(entity);
		if (fov.update) {		
			activeZone = engine.getEntitiesFor(Family.getFor(ZoneComponent.class, ActiveZoneComponent.class));
			ZoneComponent zone = activeZone.get(0).getComponent(ZoneComponent.class);
			
			if (pos.zoneID == zone.zoneID) {
				FieldOfView view = fov.fieldOfView.get(zone.zoneID);
				
				if (view == null) {
					view = new FieldOfView(zone.zone.getWidth(),zone.zone.getHeight());
					fov.fieldOfView.put(zone.zoneID, view);
				}
				
				Gdx.app.log("FieldOfView:"," Updating FOV: " + pos.pos.toString());
				
				view.resetVisible(false);
				view.resetEntityList();
				view.resetItemList();
				
				for (int x = MathUtils.floor(pos.newPos.x - fov.visibilityRadius - 1); x <  MathUtils.floor(pos.newPos.x + fov.visibilityRadius + 1); x++) {
					for (int y = MathUtils.floor(pos.newPos.y - fov.visibilityRadius - 1); y <  MathUtils.floor(pos.newPos.y + fov.visibilityRadius + 1); y++) {
						if ((pos.newPos.x - x) * (pos.newPos.x - x) + (pos.newPos.y - y) * (pos.newPos.y - y) <= ( fov.visibilityRadius *  fov.visibilityRadius)) {
							if (view.isValid(x, y) && !view.isVisible(x, y)) {
								for (Point p :new Line(MathUtils.floor(pos.newPos.x), MathUtils.floor(pos.newPos.y), x, y)){
									view.setVisible(p.x(),p.y(), true);
									view.setRemembered(p.x(), p.y(), true);
									
									if (p.x() == pos.newPos.x && p.y() == pos.newPos.y)
										continue;
									
									Entity entityAt = engine.getSystem(CollisionSystem.class).getEntityAt(p.x(), p.y(), zone.zoneID);
									
									if (entityAt != null && entityAt.getId() != entity.getId()) {
										view.addEntity(entityAt);
										if (Mappers.turnComponent.has(entityAt))
											Mappers.turnComponent.get(entityAt).active = true;
									}
									
									Entity itemAt = engine.getSystem(ItemSystem.class).getItemsAt(p.x(), p.y(), zone.zoneID);
									
									if (itemAt != null) {
										view.addItem(itemAt);
									}
									
									if (!zone.zone.isPassable(p.x(), p.y())) {
										break;
									}
								}
							}
						}
					}
				}
			}
			fov.update = false;
		}
		
	}

	@Override
	public boolean handleMessage(Telegram msg) {
/*		if (msg.message == Messages.ENTITY_MOVED 
				|| msg.message == Messages.GAME_STARTED
				|| msg.message == Messages.ZONE_CHANGE) {
			
		}*/
		if (msg.message == Messages.ENTITY_MOVED) {
			if(msg.extraInfo != null && EntityMovedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity moved = ((EntityMovedMessage)msg.extraInfo).moved;
				if(Mappers.fieldOfViewComponent.has(moved))
					Mappers.fieldOfViewComponent.get(moved).update = true;
				return true;
			}
		}
		else if (msg.message == Messages.ZONE_CHANGE) {
			if(msg.extraInfo != null && ZoneChangeMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity moved = ((ZoneChangeMessage)msg.extraInfo).player;
				Mappers.fieldOfViewComponent.get(moved).update = true;
				return true;
			}
		}
		return false;
	}
}