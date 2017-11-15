package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.ai.EnemyBrain;

public class EnemyBrainComponent extends Component implements Poolable {
	
	public EnemyBrain enemyBrain;

	@Override
	public void reset() {
		enemyBrain = null;		
	}
}
