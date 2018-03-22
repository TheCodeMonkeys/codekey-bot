package codemonkeys.bots.codekey.modlog.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import codemonkeys.bots.codekey.main.Main;
import codemonkeys.bots.codekey.modlog.Case;
import codemonkeys.bots.codekey.modlog.DataBase;
import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.channel.IGuildTextChannel;
import io.discloader.discloader.entity.channel.ITextChannel;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.util.Permissions;
import io.discloader.discloader.entity.util.SnowflakeUtil;

/**
 * @author Perry Berman
 *
 */
public class CommandReason extends Command {
	
	private final String regex = " *(\\d*-?\\d*|latest) (.*)";
	private final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
	
	public CommandReason() {
		setUnlocalizedName("reason");
		setUsage("reason <casenumber[-casenumber]> <reason>");
		setDescription("Sets the reason of a case in the modlog");
		
	}
	
	@Override
	public void execute(MessageCreateEvent e, String[] args) throws Exception {
		if (e.getMessage().getGuild() != null || e.getMessage().getGuild().getID() == Main.config.modLogs.guildID) {
			ITextChannel logChannel = e.getMessage().getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID);
			Matcher matcher = pattern.matcher(e.getMessage().getContent());
			if (matcher.find()) {
				System.out.println("Full match: " + matcher.group(0));
				if (matcher.groupCount() < 2) {
					e.getChannel().sendMessage("Usage: " + getUsage());
					return;
				}
				long min = 0, max = 0;
				Case c = null;
				String[] caseRange = matcher.group(1).split("-");
				if (matcher.group(1).equalsIgnoreCase("latest")) {
					min = DataBase.getLatestCaseNumber();
				} else {
					min = Long.parseUnsignedLong(caseRange[0]);
					if (caseRange.length > 1) {
						max = Long.parseUnsignedLong(caseRange[1]);
					} else {
						max = min;
					}
				}
				if (max >= min) {
					for (; min <= max; min++) {
						c = DataBase.getCase(min);
						if (c == null || SnowflakeUtil.parse(c.moderator.id) != e.getMessage().getAuthor().getID())
							continue;
						c.reason = matcher.group(2);
						DataBase.modifyCase(c);
						IMessage msg = logChannel.fetchMessage(c.msgID).get();
						RichEmbed embed = RichEmbed.from(msg.getEmbeds().get(0));
						embed.getFields().get(1).setValue(matcher.group(2));
						msg.edit(embed);
					}
				}
				c = DataBase.getCase(min);
				if (c != null && SnowflakeUtil.parse(c.moderator.id) == e.getMessage().getAuthor().getID()) {
					c.reason = matcher.group(2);
					DataBase.modifyCase(c);
					IMessage msg = logChannel.fetchMessage(c.msgID).get();
					RichEmbed embed = RichEmbed.from(msg.getEmbeds().get(0));
					embed.getFields().get(1).setValue(matcher.group(2));
					msg.edit(embed);
				}
			}
		}
		e.getMessage().delete();
	}
	
	@Override
	public boolean shouldExecute(IGuildMember member, IGuildTextChannel channel) {
		return member.getPermissions().hasAny(Permissions.BAN_MEMBERS, Permissions.KICK_MEMBERS);
	}
}
