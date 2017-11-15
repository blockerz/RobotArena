package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.lofisoftware.robotarena.components.MeleeWeaponComponent;
import com.lofisoftware.robotarena.components.StatsComponent;
import com.lofisoftware.robotarena.messages.CollisionMessage;
import com.lofisoftware.robotarena.messages.DamageMessage;
import com.lofisoftware.robotarena.messages.EntityDamagedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.PushMessage;
import com.lofisoftware.robotarena.world.Direction;

public class DamageSystem implements Telegraph {

	@Override
	public boolean handleMessage(Telegram msg) {
		
		if (msg.message == Messages.ENTITY_COLLISION) {
			if(msg.extraInfo != null && CollisionMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity attacker = ((CollisionMessage)msg.extraInfo).collider;
				Entity victim = ((CollisionMessage)msg.extraInfo).collidee;
				Direction direction = ((CollisionMessage)msg.extraInfo).direction;

				/*if (isCompetitorCollision(attacker,victim)) {
					MessageDispatcher.getInstance().dispatchMessage(null, Messages.PUSH, new PushMessage(attacker, victim, direction));
					
				}
				else */
				if (canMeleeAttack(attacker,victim)) {
					basicMeleeAttack(attacker, victim);
				}
				else if (isCompetitorCollision(attacker,victim)) {
					MessageDispatcher.getInstance().dispatchMessage(null, Messages.PUSH, new PushMessage(attacker, victim, direction));
				}
			}
		}
		
		if (msg.message == Messages.DAMAGE) {
			if(msg.extraInfo != null && DamageMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity attacker = ((DamageMessage)msg.extraInfo).attacker;
				Entity victim = ((DamageMessage)msg.extraInfo).victim;
				int damage = ((DamageMessage)msg.extraInfo).damage;

				if (canAttack(victim)) {
					basicAttack(attacker, victim,damage);
				}
			}
		}
		
		return false;
	}

	private void basicAttack(Entity attacker, Entity victim, int damage) {
		
		StatsComponent victimStats = Mappers.statsComponent.get(victim);
		
		victimStats.changeHealth(-damage);

		MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_DAMAGED, new EntityDamagedMessage(victim, attacker, damage));
		
	}


	private boolean canAttack(Entity victim) {
		if (Mappers.statsComponent.has(victim))
			return true;
		return false;
	}


	private void basicMeleeAttack(Entity attacker, Entity victim) {
		
		MeleeWeaponComponent attackWeapon = Mappers.meleeWeapon.get(attacker);
		//StatsComponent attackStats = Mappers.statsComponent.get(attacker);
		StatsComponent victimStats = Mappers.statsComponent.get(victim);
		//CharacterComponent attackCharacter = Mappers.characterComponent.get(attacker);
		//CharacterComponent victimCharacter = Mappers.characterComponent.get(victim);
		
		int damage = MathUtils.floor(attackWeapon.getDamageMin() + ( MathUtils.random() * (attackWeapon.getDamageMax() - attackWeapon.getDamageMin())));
		
		victimStats.changeHealth(-damage);
		
		//if (attackCharacter != null && victimCharacter != null)
		//	Gdx.app.log("MeleeSystem:basicMeleeAttack"," " + attackCharacter.getName() + " attacks " + victimCharacter.getName() + " for " + damage );
		//Gdx.app.log("MeleeSystem:basicMeleeAttack"," victim: " + victim.toString());
		MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_DAMAGED, new EntityDamagedMessage(victim, attacker, damage));
	}


	public boolean canMeleeAttack(Entity attacker, Entity victim) {
		if (Mappers.meleeWeapon.has(attacker) && Mappers.statsComponent.has(victim))
			return true;
		return false;
	}
	
	public boolean isCompetitorCollision(Entity attacker, Entity victim) {
		if (Mappers.competitorComponent.has(attacker) && Mappers.competitorComponent.has(victim))
			return true;
		return false;
	}
}
