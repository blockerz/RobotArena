package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BoundsComponent extends Component implements Poolable {
	public final Rectangle bounds = new Rectangle();

	@Override
	public void reset() {
		bounds.set(0, 0, 0, 0);		
	}
}
