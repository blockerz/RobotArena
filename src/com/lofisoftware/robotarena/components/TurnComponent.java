package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TurnComponent extends Component implements Poolable {
	
	public static enum TURN { 
		WAITING,
		GO
	};
	
	public TURN state = TURN.WAITING;
	public int priority = 0;
	public int turn = 0;
	public boolean active;
	
	@Override
	public void reset() {
		state = TURN.WAITING;
		priority = 0;
		turn = 0;
		active = false;
	}

}
