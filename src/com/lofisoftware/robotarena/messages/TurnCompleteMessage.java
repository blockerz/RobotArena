package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class TurnCompleteMessage{
	public Entity entity;

	public TurnCompleteMessage(Entity entity) {
		this.entity = entity;
	}

}
