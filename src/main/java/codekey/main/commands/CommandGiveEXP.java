package codekey.main.commands;

import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;

public class CommandGiveEXP extends Command {
	public CommandGiveEXP() {
		super();
		setUnlocalizedName("giveexp");
		setArgsRegex("(\\d+)");
	}

	public void execute(MessageCreateEvent e, String[] args) {
		
	}
	
}
