package com.lofisoftware.robotarena.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.math.Vector3;

public class VectorAccessor implements TweenAccessor<Vector3> {

	public static final int POS_XY = 1;
	public static final int POS_X = 2;
	public static final int POS_Y = 3;
	public static final int POS_XYZ = 4;

	@Override
	public int getValues(Vector3 target, int tweenType,	float[] returnValues) {
		switch (tweenType) {
		case POS_XY:
			returnValues[0] = target.x;
			returnValues[1] = target.y;
			return 2;
		case POS_X:
			returnValues[0] = target.x;
			return 1;
		case POS_Y:
			returnValues[0] = target.y;
			return 1;
		case POS_XYZ:
			returnValues[0] = target.x;
			returnValues[1] = target.y;
			returnValues[2] = target.z;
			return 3;
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(Vector3 target, int tweenType,
			float[] newValues) {
		switch (tweenType) {
		case POS_XY:
			target.set(newValues[0], newValues[1], target.z);
			break;
		case POS_X:
			target.x = newValues[0];
			break;
		case POS_Y:
			target.y = newValues[0];
			break;
		case POS_XYZ:
			target.x = newValues[0];
			target.y = newValues[1];
			target.z = newValues[2];
			break;
		default:
			assert false;
		}		
	}
}

