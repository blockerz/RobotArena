package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;
import com.lofisoftware.robotarena.world.Direction;

public class CollisionMessage {
	
	public Entity collider, collidee;
	public Direction direction;
	
	public CollisionMessage(Entity collider, Entity collidee, Direction direction) {
		this.collider = collider;
		this.collidee = collidee;
		this.direction = direction;
	}
}
