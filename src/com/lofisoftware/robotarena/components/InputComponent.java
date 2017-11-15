package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.util.Line;

public class InputComponent extends Component implements Poolable {
	public Vector3 clickPosition;
	boolean active;
	public Line path;
	public int step = 0;
	
	public InputComponent () {
		reset();
	}
	
	@Override
	public void reset() {
		clickPosition = new Vector3(0,0,0);
		active = false;
		path = null;
		step = 0;
	}

}
