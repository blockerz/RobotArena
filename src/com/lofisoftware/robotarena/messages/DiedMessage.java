package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class DiedMessage {
	public Entity victim, attacker;
	
	public DiedMessage(Entity victim, Entity attacker) {
		this.victim = victim;
		this.attacker = attacker;
	}

}
