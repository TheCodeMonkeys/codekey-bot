package codemonkeys.bots.codekey.main.commands;

import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.message.IMessage;

public class CommandGiveEXP extends Command {
	public CommandGiveEXP() {
		super();
		setUnlocalizedName("giveexp");
		setArgsRegex("(\\d+)");
	}

	public void execute(MessageCreateEvent e, String[] args) {
		IMessage message = e.getMessage();
		IGuild guild = message.getGuild();
		if (guild == null) return;
	}
	
}
