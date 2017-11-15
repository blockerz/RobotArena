package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StatsComponent extends Component implements Poolable {

	private int maxHealth;
	private int currentHealth;
	
	private int maxMana;
	private int currentMana;
	
	private int strength; 
	
	public StatsComponent() {
		reset();
	}
	
	public StatsComponent(int maximumHealth, int maximumMana, int strength) {

		maxHealth = currentHealth = maximumHealth;
		maxMana = currentMana = maximumMana;
		this.strength = strength;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}
	
	public void changeHealth(int change) {
		if (currentHealth + change <= maxHealth)
			currentHealth = currentHealth + change;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getCurrentMana() {
		return currentMana;
	}

	public void setCurrentMana(int currentMana) {
		this.currentMana = currentMana;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	@Override
	public void reset() {
		maxHealth = currentHealth = 0;
		maxMana = currentMana = 0;
		strength = 0;
	}

}
