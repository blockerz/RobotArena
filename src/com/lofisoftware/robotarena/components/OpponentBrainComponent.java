package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.ai.OpponentBrain;

public class OpponentBrainComponent extends Component implements Poolable {
	
	public OpponentBrain opponentBrain;

	@Override
	public void reset() {
		opponentBrain = null;		
	}
}