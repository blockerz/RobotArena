package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RemoveEntityComponent extends Component implements Poolable {
	
	public float time = 0; 
	
	@Override
	public void reset() {
		time = 0;		
	}

}
