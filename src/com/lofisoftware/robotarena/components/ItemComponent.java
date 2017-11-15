package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.commands.Command;

public class ItemComponent extends Component implements Poolable {

	public enum ITEM_TYPE { REPAIR, SPECIAL };
	
	public boolean activate;
	public Command command;
	public ITEM_TYPE type;
	
	public ItemComponent () {
		reset();
	}
	
	@Override
	public void reset() {
		activate = false;
		command = null;
		//name = "";
	}
}
