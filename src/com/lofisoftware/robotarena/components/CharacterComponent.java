package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CharacterComponent extends Component implements Poolable {
	
	private String name;
	private boolean hostile = false;

	public CharacterComponent () {
		reset();
	}
	
	@Override
	public void reset() {
		hostile = false;
		setName("Unknown");
	}

	public boolean isHostile() {
		return hostile;
	}

	public void setHostile(boolean hostile) {
		this.hostile = hostile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
