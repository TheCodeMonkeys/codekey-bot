package codemonkeys.bots.codekey.modlog.commands;

import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.util.Permissions;

/**
 * @author Perry Berman
 *
 */
public class CommandReason extends Command {
	
	/**
	 * 
	 */
	public CommandReason() {
		setUnlocalizedName("reason");
	}
	
	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		return member.getPermissions().hasAny(Permissions.BAN_MEMBERS, Permissions.KICK_MEMBERS);
	}
}
