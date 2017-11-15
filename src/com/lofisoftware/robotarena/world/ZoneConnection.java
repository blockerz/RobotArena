package com.lofisoftware.robotarena.world;

import com.badlogic.ashley.core.Entity;
import com.lofisoftware.robotarena.util.Point;

public class ZoneConnection {
	public Entity connectingZone;
	public Point connectionPoint;
	public Point originPoint;
	
	public ZoneConnection(Entity connectingZone, Point connectionPoint, Point originPoint) {
		this.connectingZone = connectingZone;
		this.connectionPoint = connectionPoint;
		this.originPoint = originPoint;
	}
}
