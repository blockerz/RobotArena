package com.lofisoftware.robotarena.messages;

import com.badlogic.ashley.core.Entity;

public class FinishMessage {
	
	public Entity finisher;
	public int place;
	
	public FinishMessage(Entity finisher, int place) {
		this.finisher = finisher;
		this.place = place;
	}

}