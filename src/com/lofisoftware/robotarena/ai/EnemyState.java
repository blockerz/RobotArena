package com.lofisoftware.robotarena.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum EnemyState implements State<EnemyBrain> {

    ATTACK() {
        @Override
        public void update(EnemyBrain brain) {
            if (brain.isSafe()) {
            	Gdx.app.log("EnemyState:"," I'm feeling safe. ");
            	brain.getStateMachine().changeState(SLEEP);
            	brain.sleep();
            }
            else {
            	brain.attackEnemy();
            }
        }
    },

    SLEEP() {
        @Override
        public void update(EnemyBrain brain) {
            if (brain.isThreatened()) {
            	Gdx.app.log("EnemyState:"," I'm feeling threatened. ");
            	brain.getStateMachine().changeState(ATTACK);
            	brain.attackEnemy();
            }
            else {
            	brain.sleep();
            }
        }
    };
    
    @Override
    public void enter(EnemyBrain brain) {
    }

    @Override
    public void exit(EnemyBrain brain) {
    }

    @Override
    public boolean onMessage(EnemyBrain brain, Telegram telegram) {
        
        return false;
    }
}