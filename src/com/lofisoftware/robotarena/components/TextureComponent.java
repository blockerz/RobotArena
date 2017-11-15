package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TextureComponent extends Component implements Poolable {
	public TextureRegion region = null;
	public Color tintColor;
	public boolean tint = false;
	public boolean shadow = false;
	public boolean visible = true; 
	public float offsetX = 0f;
	public float offsetY = 0f;

	@Override
	public void reset() {
		region = null;
		tint = false;
		shadow = false;
		visible = true;
		tintColor = null;
		offsetX = 0f;
		offsetY = 0f;
	}
}
