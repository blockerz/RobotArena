package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CompetitorComponent extends Component implements Poolable {

	public int player = 0;
	public int score = 0;
	public int place = 0;
	
	@Override
	public void reset() {
		player = 0;
		score = 0;
		place = 0;
	}
}