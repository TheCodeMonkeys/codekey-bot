package codemonkeys.bots.codekey.modlog.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import codemonkeys.bots.codekey.main.Main;
import codemonkeys.bots.codekey.modlog.ModLogListener;
import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.common.exceptions.PermissionsException;
import io.discloader.discloader.core.entity.Overwrite;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.IOverwrite;
import io.discloader.discloader.entity.channel.IGuildChannel;
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.message.IMentions;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

public class CommandUnmute extends Command {
	public CommandUnmute() {
		setUnlocalizedName("unmute");
		setDescription("Unmutes the mentioned user(s) from the mentioned channel(s)");
		setFullDescription("Umutes the mentioned user(s) from the mentioned channel(s)\nForce mentioning a voice channel will un server mute the user(s).");
		setUsage("mute <@user [@user[...]]> <#channel [#channel[...]]> : <reason>");
	}

	@Override
	public void execute(MessageCreateEvent e) {
		IMessage msg = e.getMessage();
		IGuild guild = msg.getGuild();
		if (guild == null || guild.getID() != Main.config.modLogs.guildID) {
			return;
		}
		String reason = msg.getContent().split(":")[msg.getContent().split(":").length - 1].trim();
		IMentions mentions = msg.getMentions();
		if (mentions.getMembers().isEmpty() || mentions.getChannels().isEmpty()) {
			e.getChannel().sendMessage(getUsage());
			return;
		}
		List<IGuildChannel> channels = mentions.getChannels();
		List<IUser> users = new ArrayList<>();
		List<IOverwrite> ows = new ArrayList<>();
		for (IGuildChannel chan : channels) {
			ows = new ArrayList<>(chan.getOverwrites().values());
			for (IGuildMember member : mentions.getMembers()) {
				if (member.getID() == msg.getAuthor().getID()) {
					continue; // make sure you can't mute yourself.
				}
				IOverwrite current = null;
				switch (chan.getType()) {
				case TEXT:
					if ((current = chan.getOverwriteByID(member.getID())) != null) {
						ows.remove(current);
						if ((current.computePermissions().toLong() & 0x800) == 0x0) {
							ows.add(new Overwrite(current.getAllowed(), current.getDenied() & ~0x800, member));
						}
					}
					break;
				case VOICE:
					if (member.isMuted()) {
						try {
							member.unMute(reason).get();
						} catch (InterruptedException | ExecutionException ex) {
							ex.printStackTrace();
						}
					}
					break;
				default:
					break;
				}
			}
			try {
				chan.setOverwrite(reason, ows.toArray(new IOverwrite[0])).get();
			} catch (PermissionsException | InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			}
		}
		RichEmbed embed = new RichEmbed("Members Muted").setTimestamp();
		embed.setDescription("The following user(s) have been muted in the following channel(s)");
		String members = "", chnls = "";
		for (int i = 0; i < mentions.getMembers().size(); i++) {
			if (i != 0) {
				members += ", ";
			}
			members += mentions.getMembers().get(i).toMention();
		}
		for (int i = 0; i < mentions.getChannels().size(); i++) {
			if (i != 0) {
				chnls += ", ";
			}
			chnls += mentions.getChannels().get(i).toMention();
		}
		embed.addField("Member(s) Unmuted", members, true).addField("Channels", chnls, true).addField("Reason", reason, true);
		try {
			e.getChannel().sendEmbed(embed).get();
			ModLogListener.createUnmutedCase(users, channels, reason);
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		return member.hasRole(219266745729286145l);
	}

}
