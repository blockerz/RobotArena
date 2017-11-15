package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
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

public class ScoreSystem implements Telegraph{

	private Array<String> messages; 
	
	public static final int SCORE_KILL = 5;
	public static final int SCORE_REPAIR = 1;
	public static final int SCORE_KILL_COMPETITOR = 10;
	public static final int SCORE_FIRST_PLACE = 50;
	public static final int SCORE_SECOND_PLACE = 25;
	public static final int SCORE_THIRD_PLACE = 15;
	public static final int SCORE_FOURTH_PLACE = 5;
	
	public ScoreSystem () {

	}

	private void addScore(Entity scorer, int score) {
		if (Mappers.competitorComponent.has(scorer))
			Mappers.competitorComponent.get(scorer).score += score;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.ENTITY_DAMAGED) {
			if(msg.extraInfo != null && EntityDamagedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){

				Entity attacker = ((EntityDamagedMessage)msg.extraInfo).attacker;
				//addScore(attacker,SCORE_KILL);
				
				return false;
			}
		}
		else if (msg.message == Messages.ENTITY_PUSHED) {
			if(msg.extraInfo != null && EntityPushedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity pusher = ((EntityPushedMessage)msg.extraInfo).pusher;

			}
		}
		else if (msg.message == Messages.ENTITY_REPAIRED) {
			if(msg.extraInfo != null && EntityRepairedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity patient = ((EntityRepairedMessage)msg.extraInfo).patient;
				
				addScore(patient,SCORE_REPAIR);
			}
		}
		else if (msg.message == Messages.ENTITY_DIED) {
			if(msg.extraInfo != null && DiedMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity attacker = ((DiedMessage)msg.extraInfo).attacker;
				Entity victim = ((DiedMessage)msg.extraInfo).victim;

				if (Mappers.competitorComponent.has(victim))
					addScore(attacker,SCORE_KILL_COMPETITOR);
				else
					addScore(attacker,SCORE_KILL);

				
				return true;
			}
		}
		else if (msg.message == Messages.FINISHED) {
			if(msg.extraInfo != null && FinishMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity finisher = ((FinishMessage)msg.extraInfo).finisher;
				int place = ((FinishMessage)msg.extraInfo).place;
				int score = 0;
				
				switch(place) {
					case 1:
						score = SCORE_FIRST_PLACE;
						break;
					case 2:
						score = SCORE_SECOND_PLACE;
						break;
					case 3:
						score = SCORE_THIRD_PLACE;
						break;
					case 4:
						score = SCORE_FOURTH_PLACE;
						break;
					default:
						score = 0;
				}
				
				addScore(finisher,score);
				
				return true;
			}
		}

		return false;
	}
	
}