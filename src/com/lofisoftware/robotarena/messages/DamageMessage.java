package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class DamageMessage {
	
	public Entity victim, attacker;
	public int damage;
	
	public DamageMessage(Entity victim, Entity attacker, int damage) {
		this.victim = victim;
		this.attacker = attacker;
		this.damage = damage;
	}

}