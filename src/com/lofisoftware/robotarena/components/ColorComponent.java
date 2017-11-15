package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ColorComponent extends Component implements Poolable {

	public float time = 0f;
	public Color tintColor;

	@Override
	public void reset() {
		time = 0f;
		tintColor = new Color(Color.CLEAR);
	}
}