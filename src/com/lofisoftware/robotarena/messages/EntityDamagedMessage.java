package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class EntityDamagedMessage {
	
	public Entity victim, attacker;
	public int damage;
	
	public EntityDamagedMessage(Entity victim, Entity attacker, int damage) {
		this.victim = victim;
		this.attacker = attacker;
		this.damage = damage;
	}

}
