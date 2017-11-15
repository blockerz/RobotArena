package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.commands.Command;

public class HazardComponent extends Component implements Poolable {

	public boolean activate;
	public Command command;
	public int cost;
	
	public HazardComponent () {
		reset();
	}
	
	@Override
	public void reset() {
		activate = false;
		command = null;
		cost = 1;
	}
}
