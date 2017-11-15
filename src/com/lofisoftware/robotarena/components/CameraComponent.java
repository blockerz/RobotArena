package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CameraComponent extends Component implements Poolable {
	public Entity target;
	public OrthographicCamera camera;
	public float offsetX, offsetY;

	public CameraComponent() {
		reset();
	}
	
	@Override
	public void reset() {
		target = null;
		camera = null;
		offsetX = offsetY = 0;
	}
}
