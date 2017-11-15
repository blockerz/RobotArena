package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.lofisoftware.robotarena.components.AnimationComponent;
import com.lofisoftware.robotarena.components.StateComponent;
import com.lofisoftware.robotarena.components.TextureComponent;


public class AnimationSystem extends IteratingSystem {

	
	@SuppressWarnings("unchecked")
	public AnimationSystem() {
		super(Family.getFor(TextureComponent.class,
							AnimationComponent.class,
							StateComponent.class));

	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		long id = entity.getId();
		if (id > 0L) {
			TextureComponent tex = Mappers.textureComponent.get(entity);
			AnimationComponent anim = Mappers.animationComponent.get(entity);
			StateComponent state = Mappers.stateComponent.get(entity);
			
			Animation animation = anim.animations.get(state.getInt());
			
			if (animation != null) {
				tex.region = animation.getKeyFrame(state.time); 
			}
			
			state.time += deltaTime;
		}
		else 
			Gdx.app.error("AnimationSystem:", "Entity ID was LESS THAN ZERO");
	}
}
