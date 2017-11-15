package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class RepairMessage {
	
	public Entity patient;
	public String doctor;
	public int amount;
	
	public RepairMessage(Entity patient, String doctor, int amount) {
		this.patient = patient;
		this.doctor = doctor;
		this.amount = amount;
	}

}