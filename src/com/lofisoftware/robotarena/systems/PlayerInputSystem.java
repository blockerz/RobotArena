package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.RobotArena;
import com.lofisoftware.robotarena.commands.Command;
import com.lofisoftware.robotarena.commands.DoNothingCommand;
import com.lofisoftware.robotarena.commands.MoveCommand;
import com.lofisoftware.robotarena.components.CommandComponent;
import com.lofisoftware.robotarena.components.HeroComponent;
import com.lofisoftware.robotarena.components.InputComponent;
import com.lofisoftware.robotarena.components.StateComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.TurnComponent.TURN;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.TurnCompleteMessage;
import com.lofisoftware.robotarena.messages.ZoneChangeMessage;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;

public class PlayerInputSystem extends IteratingSystem implements InputProcessor, Telegraph {	

	private int deltaX = 0;
	private int deltaY = 0;
	private PooledEngine engine;
	Entity player;
	Vector3 touchScreenPosition = new Vector3();
	Vector3 touchWorldPosition = new Vector3();
	boolean touched;
	//Line playerPath;
	Point nextPoint;
	//private boolean processingCommands, allCommandsEntered = false;
	Rectangle buttonEast,buttonWest,buttonSouth,buttonNorth,buttonWait, buttonBack, buttonEnter;

	@SuppressWarnings("unchecked")
	public PlayerInputSystem(PooledEngine engine) {
		super(Family.getFor(HeroComponent.class, TransformComponent.class, StateComponent.class, InputComponent.class, CommandComponent.class));
		this.engine = engine;
		touched = false;
		nextPoint = new Point(0,0);
		buttonEast = new Rectangle (RenderingSystem.BUTTON_MOVE_EAST_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_MOVE_EAST_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*2,RenderingSystem.GUI_SCALE*2);
		buttonWest = new Rectangle (RenderingSystem.BUTTON_MOVE_WEST_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_MOVE_WEST_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*2,RenderingSystem.GUI_SCALE*2);
		buttonSouth = new Rectangle (RenderingSystem.BUTTON_MOVE_SOUTH_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_MOVE_SOUTH_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*2,RenderingSystem.GUI_SCALE*2);
		buttonNorth = new Rectangle (RenderingSystem.BUTTON_MOVE_NORTH_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_MOVE_NORTH_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*2,RenderingSystem.GUI_SCALE*2);
		buttonWait = new Rectangle (RenderingSystem.BUTTON_WAIT_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_WAIT_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*2,RenderingSystem.GUI_SCALE*2);
		buttonBack = new Rectangle (RenderingSystem.BUTTON_BACK_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_BACK_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*2,RenderingSystem.GUI_SCALE*2);
		buttonEnter = new Rectangle (RenderingSystem.BUTTON_ENTER_X*RenderingSystem.GUI_SCALE,RenderingSystem.BUTTON_ENTER_Y*RenderingSystem.GUI_SCALE,RenderingSystem.GUI_SCALE*4,RenderingSystem.GUI_SCALE*2);

	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		
		CommandComponent commandComponent = Mappers.commandComponent.get(player);
		
		if(commandComponent.processingCommands && Mappers.turnComponent.get(player) != null && Mappers.turnComponent.get(player).state == TURN.GO && Mappers.stateComponent.get(player).get() == STATE.READY) {
			
			Command nextCommand = commandComponent.getNextCommand();
			if (nextCommand != null) {
				nextCommand.execute();
				MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE, new TurnCompleteMessage(player));
			}
			else {
				//processingCommands = false;
				//allCommandsEntered = false;
				commandComponent.reset();
			}
				
		}
		/*if (deltaX != 0 || deltaY != 0) {
		InputComponent inputComponent = Mappers.inputComponent.get(player);
		TransformComponent transformComponent = Mappers.transformComponent.get(player);
		
		if (inputComponent.path != null && inputComponent.step < inputComponent.path.getPoints().size()) {
			//tm.get(entity).newPos.x = tm.get(entity).newPos.x + deltaX;
			//mm.get(entity).animate = true;
			//mm.get(entity).move.set(deltaX,deltaY,0);
			
			//if (Mappers.stateComponent.get(player).get() == STATE.MAP_COLLISION
			//		|| Mappers.stateComponent.get(player).get() == STATE.ENTITY_COLLISION) {
			//	inputComponent.path.getPoints().clear();
			//	inputComponent.step = 0;
			//}
			
			else if(Mappers.turnComponent.get(player).state == TURN.GO && Mappers.stateComponent.get(player).get() == STATE.READY) {
				nextPoint.set(inputComponent.path.getPoints().get(inputComponent.step));
				
				if (!(nextPoint.x() == transformComponent.pos.x && nextPoint.y() == transformComponent.pos.y)) {
					
					MovementComponent movement = engine.createComponent(MovementComponent.class);
					movement.animate = true;
					movement.animationTime = 0.25f;
					//movement.move.set(deltaX,deltaY,0);
					movement.move.set(nextPoint.x()-transformComponent.pos.x,nextPoint.y()-transformComponent.pos.y,0);
					player.add(movement);
					Mappers.stateComponent.get(player).set(STATE.MOVING);
					//MessageDispatcher.getInstance().dispatchMessage(null, Messages.PLAYER_MOVED);
					MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE, new TurnCompleteMessage(player));
				}
				
				inputComponent.step++;
				
				if (inputComponent.step == inputComponent.path.getPoints().size()) {
					inputComponent.path.getPoints().clear();
					inputComponent.step = 0;
				}
			}
			
		}*/
	}

	@Override
	public boolean keyDown(int keycode) {
		if (Mappers.turnComponent.get(player) != null && Mappers.turnComponent.get(player).state == TURN.GO && Mappers.stateComponent.get(player).get() == STATE.READY) {
			
			Command command = null; 
			
			switch (keycode) {
			case Input.Keys.A: 
			case Input.Keys.LEFT: 
				command = new MoveCommand(engine, player, Direction.WEST);
				break;
			case Input.Keys.D: 
			case Input.Keys.RIGHT: 
				command = new MoveCommand(engine, player, Direction.EAST);
				break;
			case Input.Keys.W: 
			case Input.Keys.UP: 
				command = new MoveCommand(engine, player, Direction.NORTH);
				break;
			case Input.Keys.S: 
			case Input.Keys.DOWN: 
				command = new MoveCommand(engine, player, Direction.SOUTH);
				break;	
			case Input.Keys.BACKSPACE:
			//case Input.Keys.DEL:
				Mappers.commandComponent.get(player).removeLastCommand();
				//Gdx.app.log("keyDown", "Command Deleted");
				Mappers.commandComponent.get(player).allCommandsEntered = false;
				break;
			case Input.Keys.ENTER:
				if (Mappers.commandComponent.get(player).allCommandsEntered) {
					//Gdx.app.log("processCommands", "Commands Processing");
					Mappers.commandComponent.get(player).processingCommands = true;	
				}
				break;
			case Input.Keys.X: 
			case Input.Keys.PERIOD: 
				command = new DoNothingCommand();
				break;	
			case Input.Keys.ESCAPE: 
				RobotArena.changeGameState(RobotArena.GAME_MENU);
				break;
			default:
				break;
			}
			
			if (command!=null) {
				CommandComponent commandComponent = Mappers.commandComponent.get(player);
				
				if (commandComponent.addCommand(command)){
					Gdx.app.log("keyDown", "Command Added");
					if (commandComponent.allCommandEntered())
						commandComponent.allCommandsEntered = true;
				}
				else {
					commandComponent.allCommandsEntered = true;
					Gdx.app.log("keyDown", "All commands Entered");
				}


				//TransformComponent transformComponent = Mappers.transformComponent.get(player);
				
				//inputComponent.path = new Line(MathUtils.floor(transformComponent.pos.x),MathUtils.floor(transformComponent.pos.y),
				//		MathUtils.floor(transformComponent.pos.x + deltaX), MathUtils.floor(transformComponent.pos.y + deltaY));
				
				//deltaX = deltaY = 0;
			}
		}
		
		Gdx.app.log("key: ", Input.Keys.toString(keycode));
		return false;
	}


	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		touchScreenPosition.set(screenX, screenY, 0);
		if (Mappers.turnComponent.get(player) != null && Mappers.turnComponent.get(player).state == TURN.GO && Mappers.stateComponent.get(player).get() == STATE.READY) {
			touchWorldPosition.set(engine.getSystem(RenderingSystem.class).getGameCameraUnproject(touchScreenPosition));
			touched = true;
			//playerPath = new Line(MathUtils.floor(engine.getSystem(RenderingSystem.class).getGameCamera().position.x),MathUtils.floor(engine.getSystem(RenderingSystem.class).getGameCamera().position.y),
			//		MathUtils.floor(touchWorldPosition.x), MathUtils.floor(touchWorldPosition.y));
			
			//Gdx.app.log("touchDown: ", "" + touchWorldPosition.x + ", " + touchWorldPosition.y);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		touchScreenPosition.set(screenX, screenY, 0);
		//Gdx.app.log("touchUp", "clicked: "+touchScreenPosition.x+", "+touchScreenPosition.y);
		
		
		if (engine.getSystem(RenderingSystem.class).isPointInGuiViewport(touchScreenPosition)) {
			
			checkUIButtons (touchScreenPosition);
			//Gdx.app.log("touchUp", "clicked: "+touchScreenPosition.x+", "+touchScreenPosition.y);
		}
		
		if (touched) {
			InputComponent inputComponent = Mappers.inputComponent.get(player);
			
			if (engine.getSystem(RenderingSystem.class).isPointInGameViewport(touchScreenPosition)) {
				
				touchWorldPosition.set(engine.getSystem(RenderingSystem.class).getGameCameraUnproject(touchScreenPosition.set(screenX, screenY, 0)));
				
				if (inputComponent != null && touchWorldPosition != null) {
					inputComponent.clickPosition.set(touchWorldPosition);
					//inputComponent.path = playerPath;
						
					//Gdx.app.log("touchUp: ", "" + inputComponent.clickPosition.x + ", " + inputComponent.clickPosition.y);
				}
			}
			else {
				if (inputComponent != null) {
					inputComponent.clickPosition.set(0,0,0);
				}
				
				
			}
			touched = false;
			

		}
		return false;
	}

	private void checkUIButtons(Vector3 touchScreenPosition) {

		Vector3 worldPosition = engine.getSystem(RenderingSystem.class).getGuiCameraUnproject(touchScreenPosition);
		
		//Gdx.app.log("button", "rext: "+buttonEast.toString());
		
		if (buttonEast.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.D);
		else if (buttonWest.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.A);
		else if (buttonNorth.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.W);
		else if (buttonSouth.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.S);
		else if (buttonWait.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.X);
		else if (buttonBack.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.BACKSPACE);
		else if (buttonEnter.contains(worldPosition.x,worldPosition.y))
			keyDown(Input.Keys.ENTER);
				//keyDown(Input.Keys.D);
				
		
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		touchScreenPosition.set(screenX, screenY, 0);
		
		if (touched && engine.getSystem(RenderingSystem.class).isPointInGameViewport(touchScreenPosition)) {
			touchWorldPosition.set(engine.getSystem(RenderingSystem.class).getGameCameraUnproject(touchScreenPosition));

			//playerPath = new Line(MathUtils.floor(engine.getSystem(RenderingSystem.class).getGameCamera().position.x),MathUtils.floor(engine.getSystem(RenderingSystem.class).getGameCamera().position.y),
			//		MathUtils.floor(touchWorldPosition.x), MathUtils.floor(touchWorldPosition.y));
			
			//Gdx.app.log("touchDrag: ", "" + touchWorldPosition.x + ", " + touchWorldPosition.y);
		}
		else {
			//playerPath = null;
			touched = false;
		}
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.ZONE_CHANGE) {
			if(msg.extraInfo != null && ZoneChangeMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				//Entity oldZone = ((ZoneChanged)msg.extraInfo).oldZone;
				Entity newZone = ((ZoneChangeMessage)msg.extraInfo).newZone;
				Point position = ((ZoneChangeMessage)msg.extraInfo).newPosition;
				
				while(RobotArena.getTweenManager().getRunningTweensCount()>0)
					RobotArena.getTweenManager().update(0.1f);
				
				TransformComponent transComponent = Mappers.transformComponent.get(player);
				transComponent.pos.x = transComponent.newPos.x = position.x();
				transComponent.pos.y = transComponent.newPos.y = position.y();
				transComponent.zoneID = newZone.getId();
				
				return true;
			}
		}
		//else if (msg.message == Messages.TURN_COMPLETE) {
		//	Mappers.turnComponent.get(player).state = TURN.GO;
		//	return true;
		//}
		//else if (msg.message == Messages.GAME_STARTED) {
		//	Mappers.turnComponent.get(player).state = TURN.GO;
		//}
		return false;
	}

	public Entity getPlayer() {
		return player;
	}

	public void setPlayer(Entity player) {
		this.player = player;
	}

	public Vector3 getTouchWorldPosition() {
		return touchWorldPosition;
	}

	public boolean isTouched() {
		return touched;
	}
	
	//public Line getPlayerPath() {
	//	return playerPath;
	//}
}
