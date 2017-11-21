package codekey.main.commands;

import codekey.level.Player;
import codekey.level.PlayerUtils;
import codekey.level.Rank;
import codekey.main.Main;
import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.channel.ChannelTypes;
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

public class CommandStatus extends Command {

	public CommandStatus() {
		setUnlocalizedName("status");

	}

	@Override
	public void execute(MessageCreateEvent e, String[] args) {
		if (e.getChannel().getType() != ChannelTypes.TEXT) {
			return;
		}
		IMessage message = e.getMessage();
		IUser user = message.getMentions().size() > 0 ? message.getMentions().getUsers().get(0) : message.getAuthor();
		Player player = Main.players.get(user.getID());
		if (player == null)
			return;
		Rank current = PlayerUtils.getRankFromExp(player.getExp());
		RichEmbed embed = new RichEmbed("Status").setAuthor(user.getUsername(), "", user.getAvatar().toString());
		embed.addField("Current Rank", current, true);
		embed.addField("Present EXP", player.getExp(), true).addField("Next Rank", PlayerUtils.getNextRank(current), true);
		embed.addField("EXP Needed", PlayerUtils.expNeededForNextRank(player.getExp()), true);
		e.getChannel().sendEmbed(embed);
	}

	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		return true;
	}

}
