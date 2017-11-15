package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class EntityMovedMessage {
	public Entity moved;
	
	public EntityMovedMessage(Entity moved) {
		this.moved = moved;
	}

}