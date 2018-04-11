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
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

public class CommandGiveEXP extends Command {
	public CommandGiveEXP() {
		super();
		setUnlocalizedName("giveexp");
		setArgsRegex("<@\\d+> ?(-?\\d+\\.?\\d+)");
		setUsage("giveexp <@user [@user]> <exp>");
		setDescription("Gives the mentioned user exp");
		this.setFullDescription("Gives the mentioned user exp.\nGiving someone a negative amount of EXP will take EXP away from them.");
	}

	public void execute(MessageCreateEvent e, String[] ignored) {
		IMessage message = e.getMessage();
		IGuild guild = message.getGuild();
		String[] args = message.getContent().split(" ");
		if (guild == null || guild.getID() != 201544496654057472l || Main.players == null || args.length < 2 || message.getMentions().getUsers().size() < 1) {
			e.getChannel().sendMessage(getUsage());
			return;
		}
		String expArg = args[args.length - 1];
		if (expArg.contains("<") || expArg.contains("@") || expArg.contains(">")) {
			e.getChannel().sendMessage(getUsage());
			return;
		}

		double exp = Double.parseDouble(expArg);
		double currentEXP = 0.0;
		for (IUser user : message.getMentions().getUsers()) {
			Player player = Main.players.get(user.getID());
			if (player == null) {
				player = PlayerUtils.addNewPlayerEntryWithRank(user.getID(), guild.getMember(user.getID()).getRoles());
			}
			currentEXP = player.getExp();
			player.addExp(exp, message);
			DataBase.savePlayer(guild, player);
			Rank current = player.getRank();
			RichEmbed embed = new RichEmbed("EXP Given").setAuthor(user.getUsername(), "", user.getAvatar().toString()).setColor(current.getColor());
			embed.setDescription("Displaying User Status");
			embed.addField("Previous EXP", (int) currentEXP, true).addField("Present EXP", (int) player.getExp(), true).addField("EXP Given", (int) exp, true);
			embed.addField("Current Rank", current, true);// .addField("Present EXP", (int) player.getExp(), true);
			embed.addField("Next Rank", PlayerUtils.getNextRank(current), true).addField("EXP Needed", (int) PlayerUtils.expNeededForNextRank(player.getExp()), true);
			embed.setFooter(Main.getCopyright()).setTimestamp();
			e.getChannel().sendEmbed(embed);
		}
		try {
			Listener.writeToJSON();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		if (!member.hasRole(member.getGuild().getRoleByID(219266745729286145l))) {
			channel.sendMessage("Only staff members can give people exp");
			return false;
		}
		return true;
	}
}
