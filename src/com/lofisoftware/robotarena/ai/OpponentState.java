package com.lofisoftware.robotarena.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.lofisoftware.robotarena.systems.Mappers;

public enum OpponentState implements State<OpponentBrain> {

    ATTACK_RIVAL() {
        @Override
        public void update(OpponentBrain brain) {
            if (brain.isThreatenedByCompetitor()) {
            	Gdx.app.log("OpponentState:"," Still attacking rival ");
            	brain.attackTarget();
            }
            else {
            	
            	brain.getStateMachine().changeState(RACE);
            	brain.race(true);
            }
        }
    },
    
    ATTACK_ENEMY() {
        @Override
        public void update(OpponentBrain brain) {
            if (brain.isThreatenedByEnemy()) {
            	Gdx.app.log("OpponentState:"," Still attacking enemy ");
            	brain.attackTarget();
            }
            else {
            	
            	brain.getStateMachine().changeState(RACE);
            	brain.race(true);
            }
        }
    },
    
    ITEM() {
        @Override
        public void update(OpponentBrain brain) {
        	if (brain.isThreatenedByEnemy()) {
            	brain.getStateMachine().changeState(ATTACK_ENEMY);
            	brain.attackTarget();
        	}
        	else if (brain.isNeededItemNear()) {
            	Gdx.app.log("OpponentState:"," Picking up item ");
            	brain.attackTarget();
            }
            else {
            	
            	brain.getStateMachine().changeState(RACE);
            	brain.race(true);
            }
        }
    },

    RACE() {
        @Override
        public void update(OpponentBrain brain) {

        	if (brain.isThreatenedByEnemy()) {
            	brain.getStateMachine().changeState(ATTACK_ENEMY);
            	brain.attackTarget();
        	}
        	else if (brain.isNeededItemNear()) {
            	brain.getStateMachine().changeState(ITEM);
            	brain.attackTarget();
        	}
        	else if (brain.isThreatenedByCompetitor()) {
            	brain.getStateMachine().changeState(ATTACK_RIVAL);
            	brain.attackTarget();
            }
            else {
            	Gdx.app.log("OpponentState:"," I'm racing! ");
            	brain.race(false);
            }
        }
    };
    
    @Override
    public void enter(OpponentBrain brain) {
    }

    @Override
    public void exit(OpponentBrain brain) {
    }

    @Override
    public boolean onMessage(OpponentBrain brain, Telegram telegram) {
        
        return false;
    }
}