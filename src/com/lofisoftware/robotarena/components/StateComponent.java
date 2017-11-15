package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StateComponent extends Component implements Poolable {
	
	public static enum STATE { 
		READY,
		MOVING,
		MOVE_QUEUED,
		WAITING,
		MAP_COLLISION,
		ENTITY_COLLISION,
		DEAD
	};
	
	private STATE state = STATE.READY;
	public float time = 0.0f;
	
	public STATE get() {
		return state;
	}
	
	public int getInt() {
		return state.ordinal();
	}
	
	public void set(STATE newState) {
		state = newState;
		time = 0.0f;
		//Gdx.app.log("StateComponent:"," newState: " + state.name());
	}

	@Override
	public void reset() {
		state = STATE.READY;
		time = 0.0f;
	}
}
