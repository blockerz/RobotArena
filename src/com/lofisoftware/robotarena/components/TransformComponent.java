package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TransformComponent extends Component implements Poolable {
	public final Vector3 pos = new Vector3();
	public final Vector3 newPos = new Vector3();
	public final Vector2 scale = new Vector2(1.0f, 1.0f);
	public float rotation = 0.0f;
	public long zoneID = 0L;
	public boolean moving = false;
	
	@Override
	public void reset() {
		pos.set(0,0,0);
		newPos.set(0,0,0);
		scale.set(1.0f, 1.0f);
		rotation = 0.0f;
		zoneID = 0L;
		moving = false;
	}
}
