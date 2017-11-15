package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lofisoftware.robotarena.components.ColorComponent;
import com.lofisoftware.robotarena.components.TextureComponent;

public class TextureColorSystem extends IteratingSystem {

	
	@SuppressWarnings("unchecked")
	public TextureColorSystem() {
		super(Family.getFor(TextureComponent.class, ColorComponent.class));

	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {

		Mappers.colorComponent.get(entity).time -= deltaTime;
		
		if (Mappers.colorComponent.get(entity).time > 0) {
			Mappers.textureComponent.get(entity).tint = true;
			Mappers.textureComponent.get(entity).tintColor = Mappers.colorComponent.get(entity).tintColor;
		}
		else {
			Mappers.textureComponent.get(entity).tint = false;
			Mappers.textureComponent.get(entity).tintColor = null;
			entity.remove(ColorComponent.class);
		}
			
	}
}
