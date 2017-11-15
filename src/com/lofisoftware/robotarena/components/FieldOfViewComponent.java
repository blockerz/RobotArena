package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.world.FieldOfView;

public class FieldOfViewComponent extends Component implements Poolable {
	
	//public static final int STATE_ACTIVE = 0;
	
	public ArrayMap<Long,FieldOfView> fieldOfView;
	public int visibilityRadius = 0;
	public boolean update = true;
	
	public FieldOfViewComponent() {
		fieldOfView = new ArrayMap<Long,FieldOfView>();
	}

	@Override
	public void reset() {
		fieldOfView.clear(); 
		visibilityRadius = 0;
		update = true;
	}
}
