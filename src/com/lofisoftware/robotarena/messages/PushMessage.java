package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;
import com.lofisoftware.robotarena.world.Direction;

public class PushMessage {
	
	public Entity pusher, pushee;
	public Direction direction;
	
	public PushMessage(Entity pusher, Entity pushee, Direction direction) {
		this.pusher = pusher;
		this.pushee = pushee;
		this.direction = direction;
	}

}
