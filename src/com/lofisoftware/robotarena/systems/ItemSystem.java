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
import com.lofisoftware.robotarena.components.ItemComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.messages.Messages;

public class ItemSystem extends IteratingSystem implements Telegraph {

	private ImmutableArray<Entity> itemEntities;
	
	@SuppressWarnings("unchecked")
	public ItemSystem() {
		super(Family.getFor(ItemComponent.class));
		
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {

	}
	

	
	@Override
	public boolean handleMessage(Telegram msg) {
		
		if (msg.message == Messages.ROUND_COMPLETE && itemEntities != null) {
			for (int e = 0; e < itemEntities.size(); e++) {
				Entity entity = itemEntities.get(e);
				Mappers.itemComponent.get(entity).activate = true;			
				Mappers.itemComponent.get(entity).command.execute();
				Mappers.itemComponent.get(entity).activate = false;
			}
		}
		return false;
	}
	
	public Entity getItemsAt(int x, int y, long zoneID) {
		Array<Entity> entities = getItemsAt(new Rectangle(x,y,1,1), zoneID);
		if (entities.size >= 1)
			return entities.get(0);
		return null;
	}
	
	public Array<Entity> getItemsAt(Rectangle bounds, long zoneID) {
		Array<Entity> entities = new Array<Entity>();
		for (int i = 0; i < itemEntities.size(); ++i) {
			Entity itemEntity = itemEntities.get(i);
			TransformComponent position = Mappers.transformComponent.get(itemEntity);
			
			if (zoneID != position.zoneID || entities.contains(itemEntity, true))
				continue;
			
			if (bounds.contains(MathUtils.floor(position.newPos.x), MathUtils.floor(position.newPos.y))) {				
				Gdx.app.log("ItemSystem:getEntitiesAt"," Item Found: " + bounds.toString());

				entities.add(itemEntity);
			}	 
		}
		return entities;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		itemEntities = engine.getEntitiesFor(Family.getFor(ItemComponent.class));
		

	}
}