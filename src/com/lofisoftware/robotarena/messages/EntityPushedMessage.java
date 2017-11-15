package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class EntityPushedMessage {
	
	public Entity pusher, pushee;
	
	public EntityPushedMessage(Entity pusher, Entity pushee) {
		this.pusher = pusher;
		this.pushee = pushee;
	}

}