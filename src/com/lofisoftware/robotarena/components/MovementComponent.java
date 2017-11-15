package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MovementComponent extends Component implements Poolable{
	
	public final Vector3 move = new Vector3();
	public float animationTime = 0.5f;
	public boolean animate = false;
	
	@Override
	public void reset() {
		move.set(0, 0, 0);
	}
}