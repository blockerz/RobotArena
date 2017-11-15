package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.lofisoftware.robotarena.components.StatsComponent;
import com.lofisoftware.robotarena.messages.EntityRepairedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.RepairMessage;

public class RepairSystem implements Telegraph {

	@Override
	public boolean handleMessage(Telegram msg) {
		
		if (msg.message == Messages.REPAIR) {
			if(msg.extraInfo != null && RepairMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				Entity patient = ((RepairMessage)msg.extraInfo).patient;
				String doctor = ((RepairMessage)msg.extraInfo).doctor;
				int amount = ((RepairMessage)msg.extraInfo).amount;

				if (canRepair(patient)) {
					repair(doctor, patient,amount);
				}
			}
		}
		
		return false;
	}

	
	private void repair(String doctor, Entity patient, int amount) {
		
		StatsComponent patientStats = Mappers.statsComponent.get(patient);
		
		patientStats.changeHealth(amount);

		MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_REPAIRED, new EntityRepairedMessage(patient, doctor, amount));
		
	}


	private boolean canRepair(Entity patient) {
		if (Mappers.statsComponent.has(patient))
			return true;
		return false;
	}


	
}