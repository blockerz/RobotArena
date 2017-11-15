package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;
import com.lofisoftware.robotarena.util.Point;

public class ZoneChangeMessage {

	public Entity player, oldZone, newZone;
	public Point newPosition;
	
	public ZoneChangeMessage(Entity player, Entity oldZone, Entity newZone, Point newPosition) {
		this.player = player;
		this.oldZone = oldZone;
		this.newZone = newZone;
		this.newPosition = newPosition;
	}
	
}
