package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Array;
import com.lofisoftware.robotarena.components.CharacterComponent;
import com.lofisoftware.robotarena.messages.DiedMessage;
import com.lofisoftware.robotarena.messages.EntityDamagedMessage;
import com.lofisoftware.robotarena.messages.EntityPushedMessage;
import com.lofisoftware.robotarena.messages.EntityRepairedMessage;
import com.lofisoftware.robotarena.messages.FinishMessage;
import com.lofisoftware.robotarena.messages.Messages;

public class TerminalSystem extends EntitySystem implements Telegraph{

	private Array<String> messages; 
	
	public TerminalSystem () {
		messages = new Array<String>(); 
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.ENTITY_DAMAGED) {
			if(msg.extraInfo != null && EntityDamagedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity damageVictim = ((EntityDamagedMessage)msg.extraInfo).victim;
				Entity damageAttacker = ((EntityDamagedMessage)msg.extraInfo).attacker;
				int damage = ((EntityDamagedMessage)msg.extraInfo).damage;

				CharacterComponent characterComponentVictim = Mappers.characterComponent.get(damageVictim);
				CharacterComponent characterComponentAttacker = Mappers.characterComponent.get(damageAttacker);
				
				String message = characterComponentAttacker.getName() + " hits " + characterComponentVictim.getName();
				
				if (Mappers.heroComponent.has(damageAttacker))
					message = characterComponentAttacker.getName() + " hit " + characterComponentVictim.getName();
				
				messages.add(message);
				
				return false;
			}
		}
		else if (msg.message == Messages.ENTITY_PUSHED) {
			if(msg.extraInfo != null && EntityPushedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity pusher = ((EntityPushedMessage)msg.extraInfo).pusher;
				Entity pushee = ((EntityPushedMessage)msg.extraInfo).pushee;

				if (Mappers.characterComponent.has(pusher) && Mappers.characterComponent.has(pushee) && Mappers.competitorComponent.has(pushee)) {
					CharacterComponent characterComponentVictim = Mappers.characterComponent.get(pusher);
					CharacterComponent characterComponentAttacker = Mappers.characterComponent.get(pushee);
					
					String message = characterComponentVictim.getName() + " pushes " + characterComponentAttacker.getName();
					
					if (Mappers.heroComponent.has(pusher))
						message = characterComponentVictim.getName() + " push " + characterComponentAttacker.getName();
					
					messages.add(message);
				}
				return false;
			}
		}
		else if (msg.message == Messages.ENTITY_REPAIRED) {
			if(msg.extraInfo != null && EntityRepairedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity patient = ((EntityRepairedMessage)msg.extraInfo).patient;
				String doctor = ((EntityRepairedMessage)msg.extraInfo).doctor;
				int amount = ((EntityRepairedMessage)msg.extraInfo).amount;

				CharacterComponent characterComponentPatient = Mappers.characterComponent.get(patient);
				
				
				String message = characterComponentPatient.getName() + " is repaired by " + doctor;
				
				if (Mappers.heroComponent.has(patient))
					message = characterComponentPatient.getName() + " are repaired by " + doctor;
				
				messages.add(message);
				
				return false;
			}
		}
		else if (msg.message == Messages.ENTITY_DIED) {
			if(msg.extraInfo != null && DiedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity damageVictim = ((DiedMessage)msg.extraInfo).victim;


				CharacterComponent characterComponentVictim = Mappers.characterComponent.get(damageVictim);
				
				String message = characterComponentVictim.getName() + " died. ";
				
				if (Mappers.competitorComponent.has(damageVictim))
					message = characterComponentVictim.getName() + " has malfunctioned! ";
				
				if (Mappers.heroComponent.has(damageVictim))
					message = characterComponentVictim.getName() + " have malfunctioned! ";
				
				messages.add(message);
				
				return true;
			}
		}
		else if (msg.message == Messages.FINISHED) {
			if(msg.extraInfo != null && FinishMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity finisher = ((FinishMessage)msg.extraInfo).finisher;
				int place = ((FinishMessage)msg.extraInfo).place;


				CharacterComponent characterComponentVictim = Mappers.characterComponent.get(finisher);
				
				String message = characterComponentVictim.getName() + " finished the race in ";
				
				switch(place) {
					case 1:
						message += "1st!";
						break;
					case 2:
						message += "2nd!";
						break;
					case 3:
						message += "3rd.";
						break;
					case 4:
						message += "4th.";
						break;
					default:
						message += "amazing time!";
				}
				
				messages.add(message);
				
				return true;
			}
		}
		else if (msg.message == Messages.GAME_STARTED) {
			String message = "Program your instructions and hit Enter. ";
			messages.add(message);
			return true;
		}
		else if (msg.message == Messages.GAME_OVER) {
			String message = "Game Over! ";
			messages.add(message);
			return true;
		}
		return false;
	}

	public Array<String> getMessages() {
		return messages;
	}
	
	
}