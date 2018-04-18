package codemonkeys.bots.codekey.main.commands;

import java.io.IOException;

import codemonkeys.bots.codekey.level.Player;
import codemonkeys.bots.codekey.level.PlayerUtils;
import codemonkeys.bots.codekey.level.Rank;
import codemonkeys.bots.codekey.main.DataBase;
import codemonkeys.bots.codekey.main.Listener;
import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.channel.ChannelTypes;
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

public class CommandStatus extends Command {

	public CommandStatus() {
		super();
		setUnlocalizedName("status");
		setDescription("Displays your or the mentioned user's player info.");
		setUsage("status [@user]");
		setTextureName("codekey:status");
	}

	@Override
	public void execute(MessageCreateEvent e, String[] args) {
		if (e.getChannel().getType() != ChannelTypes.TEXT) {
			return;
		}
		IMessage message = e.getMessage();
		IUser user = message.getMentions().size() > 0 ? message.getMentions().getUsers().get(0) : message.getAuthor();

		Player player = Main.players.get(user.getID());
		// create new a player entry if an entry wasn't found in the players list
		if (player == null) {
			IGuild guild = message.getGuild();
			if (guild == null || guild.getID() != 201544496654057472l) // if not run in a guild or the guild is not codemonkey's, return.
				return;
			player = PlayerUtils.addNewPlayerEntryWithRank(user.getID(), guild.getMember(user.getID()).getRoles()); // create the new entry and set player to the entry
			try {
				DataBase.savePlayer(guild, player);
				Listener.writeToJSON();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		Rank current = PlayerUtils.getRankFromExp(player.getExp());
		RichEmbed embed = new RichEmbed("Status").setAuthor(user.getUsername(), "", user.getAvatar().toString()).setColor(player.getColor());
		embed.setDescription("Displaying User Status");
		embed.addField("Current Rank", current, true).addField("Present EXP", (int) player.getExp(), true);
		embed.addField("Next Rank", PlayerUtils.getNextRank(current), true).addField("EXP Needed", (int) PlayerUtils.expNeededForNextRank(player.getExp()), true);
		embed.setFooter(Main.getCopyright()).setTimestamp();
		e.getChannel().sendEmbed(embed);
	}

	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		return true;
	}

}
