package com.lofisoftware.robotarena.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lofisoftware.robotarena.commands.Command;

public class CommandComponent extends Component implements Poolable {

	private Array<Command> commands;
	private int maxCommmands = 5;
	private int step = -1;

	public boolean processingCommands, allCommandsEntered = false;
	
	public CommandComponent () {
		commands = new Array<Command>();
		reset();
	}
	
	@Override
	public void reset() {
		commands.clear();
		step = -1;
		processingCommands = allCommandsEntered = false;
	}

	public int getStep() {
		return step;
	}
	
	public boolean addCommand(Command command) {
		if (commands.size < maxCommmands) {
			commands.add(command);
			return true;
		}
		return false;
	}

	public boolean removeLastCommand() {
		if (commands.size > 0) {
			commands.pop();
			return true;
		}
		return false;
	}

	public void removeAllCommands() {
		commands.clear();
		
	}
	
	public Command getNextCommand() {
		step++;
		return (step == maxCommmands)?null:commands.get(step);
	}
	
	public boolean allCommandEntered() {
		return commands.size == maxCommmands;
	}
	
	public int getMaxCommmands() {
		return maxCommmands;
	}

	public void setMaxCommmands(int maxCommmands) {
		this.maxCommmands = maxCommmands;
	}

	public Array<Command> getCommands() {
		return commands;
	}
}