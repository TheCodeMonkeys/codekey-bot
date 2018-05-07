package codemonkeys.bots.codekey.modlog.commands;

import java.util.concurrent.ExecutionException;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.common.exceptions.PermissionsException;
import io.discloader.discloader.core.entity.Overwrite;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.IOverwrite;
import io.discloader.discloader.entity.channel.ChannelTypes;
import io.discloader.discloader.entity.channel.IGuildChannel;
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.message.IMentions;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.util.Permissions;

public class CommandMute extends Command {

	public CommandMute() {
		super();
		setUnlocalizedName("mute");
		setDescription("Mutes the mentioned user(s) from the mentioned channel(s)");
		setFullDescription("Mutes the mentioned user(s) from the mentioned channel(s)\nForce mentioning a voice channel will server mute the user(s).");
		setUsage("mute <@user [@user[...]]> <#channel [#channel[...]]> <reason>");
	}

	@Override
	public void execute(MessageCreateEvent e) {
		IMessage msg = e.getMessage();
		IGuild guild = msg.getGuild();
		if (guild == null || guild.getID() != Main.config.modLogs.guildID) {
			return;
		}
		IMentions mentions = msg.getMentions();
		if (mentions.getMembers().isEmpty() || mentions.getChannels().isEmpty()) {
			e.getChannel().sendMessage(getUsage());
			return;
		}
		for (IGuildMember member : mentions.getMembers()) {
			if (member.getID() == msg.getAuthor().getID()) {
				continue; // make sure you can't mute yourself.
			}
			IOverwrite mutedPerms = new Overwrite(0, 0, member);
			mutedPerms.setDenied(Permissions.SEND_MESSAGES);
			for (IGuildChannel chan : mentions.getChannels()) {
				try {
					if (chan.getType() == ChannelTypes.TEXT) {
						chan.setOverwrite(mutedPerms, "Muting member").get();
					} else if (chan.getType() == ChannelTypes.VOICE) {
						member.mute("Muting Member").get();
					}
				} catch (PermissionsException | InterruptedException | ExecutionException e1) {
					e1.printStackTrace();
					e.getChannel().sendMessage("Failed to mute the user `" + member.toString() + "` in the channel `" + chan.getName() + "`");
					return;
				}
			}
		}
		RichEmbed embed = new RichEmbed("Members Muted").setTimestamp();
		embed.setDescription("The following user(s) have been muted in the following channel(s)");
		String members = "", channels = "";
		for (int i = 0; i < mentions.getMembers().size(); i++) {
			if (i != 0) {
				members += ", ";
			}
			members += mentions.getMembers().get(i).toMention();
		}
		for (int i = 0; i < mentions.getChannels().size(); i++) {
			if (i != 0) {
				channels += ", ";
			}
			channels += mentions.getChannels().get(i).toMention();
		}
		embed.addField("Muted", members, true);
		embed.addField("Channels", channels, true);
		e.getChannel().sendEmbed(embed);
	}

	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		return member.hasRole(219266745729286145l);
	}

}
