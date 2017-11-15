package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MeleeWeaponComponent  extends Component implements Poolable {
	
	public static enum MELEE_WEAPON {
		SPIKE, UNARMED
	}
	
	private MELEE_WEAPON weaponType;
	private float damageMin;
	private float damageMax;
	
	public MeleeWeaponComponent() {
		reset();
	}
	
	public MeleeWeaponComponent(MELEE_WEAPON type, float min, float max) {
		weaponType = type;
		damageMin = min;
		damageMax = max;
	}
	
	public MELEE_WEAPON getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(MELEE_WEAPON weaponType) {
		this.weaponType = weaponType;
	}

	public float getDamageMin() {
		return damageMin;
	}

	public void setDamageMin(float damageMin) {
		this.damageMin = damageMin;
	}

	public float getDamageMax() {
		return damageMax;
	}

	public void setDamageMax(float damageMax) {
		this.damageMax = damageMax;
	}

	@Override
	public void reset() {
		weaponType = MELEE_WEAPON.UNARMED;
		damageMin = 0;
		damageMax = 0;
	}

}
