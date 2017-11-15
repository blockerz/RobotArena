package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Zone;
import com.lofisoftware.robotarena.world.ZoneConnection;

public class ZoneComponent extends Component implements Poolable {
	
	public Zone zone;
	public long zoneID = 0L;
	public ArrayMap<Point,ZoneConnection> connections;
	
	public ZoneComponent() {
		super();
		reset();
	}	

	@Override
	public void reset() {
		zone = null;
		zoneID = 0L;
		connections = new ArrayMap<Point,ZoneConnection>();
	}

}
