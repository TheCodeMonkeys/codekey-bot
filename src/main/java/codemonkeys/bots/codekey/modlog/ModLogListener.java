package codemonkeys.bots.codekey.modlog;

import java.util.concurrent.CompletableFuture;

import codemonkeys.bots.codekey.main.DataBase;
import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.common.event.EventListenerAdapter;
import io.discloader.discloader.common.event.ReadyEvent;
import io.discloader.discloader.common.event.guild.GuildBanAddEvent;
import io.discloader.discloader.common.event.guild.GuildBanRemoveEvent;
import io.discloader.discloader.common.event.guild.member.GuildMemberRemoveEvent;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.auditlog.ActionTypes;
import io.discloader.discloader.entity.auditlog.IAuditLog;
import io.discloader.discloader.entity.auditlog.IAuditLogEntry;
import io.discloader.discloader.entity.util.SnowflakeUtil;

/**
 * @author Perry Berman
 *
 */
public class ModLogListener extends EventListenerAdapter {

	private long getNextCaseNumber() {
		return DataBase.getLatestCaseNumber() + 1;
	}

	private String getReasonText(long caseNumber) {
		return String.format("No reason provided. Use `%sreason %d <reason>` to change the reason.", Main.config.prefix, caseNumber);
	}

	@Override
	public void Ready(ReadyEvent e) {
		DataBase.connect();
	}

	@Override
	public void GuildBanAdd(GuildBanAddEvent e) {
		if (e.getGuild().getID() != Main.config.modLogs.guildID) {
			return;
		}
		e.getGuild().getAuditLog(ActionTypes.MEMBER_BAN_ADD, 1).thenAcceptAsync(aLogs -> {
			long caseNumber = getNextCaseNumber();
			if (aLogs.getEntries().size() > 0) {
				IAuditLogEntry entry = aLogs.getEntries().get(0);
				if (entry.getTargetID() != e.getBannedUser().getID()) { // make sure that the member was actually kicked
					final String reason = getReasonText(caseNumber);
					RichEmbed embed = new RichEmbed().setAuthor("Member Banned", "https://thecodemonkeys.net", e.getBannedUser().getAvatar().toString());
					embed.setColor(0xd0021b).setFooter("Case #" + caseNumber).setTimestamp();
					embed.addField("Member", String.format("%s (%d) (%s)", e.getBannedUser(), e.getBannedUser().getID(), e.getBannedUser()));
					embed.addField("Reason", reason);
					embed.addField("Responsible Moderator", "Unknown Moderator");
					e.getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID).sendEmbed(embed).thenAcceptAsync(msg -> {
						DataBase.createCase(caseNumber, (byte) 0, reason, e.getBannedUser(), null, msg);
					});
					return; // and return early if they weren't
				}
				final String reason = entry.getReason() == null ? getReasonText(caseNumber) : entry.getReason();
				RichEmbed embed = new RichEmbed().setAuthor("Member Banned", "https://thecodemonkeys.net", e.getBannedUser().getAvatar().toString());
				embed.setColor(0xd0021b).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d) (%s)", e.getBannedUser(), e.getBannedUser().getID(), e.getBannedUser().toMention()));
				embed.addField("Reason", reason);
				embed.addField("Responsible Moderator", entry.getAuthor());
				e.getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID).sendEmbed(embed).thenAcceptAsync(msg -> {
					DataBase.createCase(caseNumber, (byte) 0, reason, e.getBannedUser(), entry.getAuthor(), msg);
				});
			}
		});
	}

	@Override
	public void GuildBanRemove(GuildBanRemoveEvent e) {
		if (e.getGuild().getID() != Main.config.modLogs.guildID) {
			return;
		}
		e.getGuild().getAuditLog(ActionTypes.MEMBER_BAN_REMOVE, 1).thenAcceptAsync(aLogs -> {
			long caseNumber = getNextCaseNumber();
			if (aLogs.getEntries().size() > 0) {
				IAuditLogEntry entry = aLogs.getEntries().get(0);
				if (entry.getTargetID() != e.getUnbannedUser().getID()) { // make sure that the member was actually kicked
					final String reason = getReasonText(caseNumber);
					RichEmbed embed = new RichEmbed().setAuthor("Member Unbanned", "https://thecodemonkeys.net", e.getUnbannedUser().getAvatar().toString());
					embed.setColor(0x00ff00).setFooter("Case #" + caseNumber).setTimestamp();
					embed.addField("Member", String.format("%s (%d) (%s)", e.getUnbannedUser(), e.getUnbannedUser().getID(), e.getUnbannedUser().toMention()));
					embed.addField("Reason", reason);
					embed.addField("Responsible Moderator", "Unknown Moderator");
					e.getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID).sendEmbed(embed).thenAcceptAsync(msg -> {
						DataBase.createCase(caseNumber, (byte) 2, reason, e.getUnbannedUser(), null, msg);
					});
					return; // and return early if they weren't
				}
				final String reason = entry.getReason() == null ? getReasonText(caseNumber) : entry.getReason();
				RichEmbed embed = new RichEmbed().setAuthor("Member Unbanned", "https://thecodemonkeys.net", e.getUnbannedUser().getAvatar().toString());
				embed.setColor(0x00ff00).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d) (%s)", e.getUnbannedUser(), e.getUnbannedUser().getID(), e.getUnbannedUser().toMention()));
				embed.addField("Reason", reason);
				embed.addField("Responsible Moderator", entry.getAuthor());
				e.getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID).sendEmbed(embed).thenAcceptAsync(msg -> {
					DataBase.createCase(caseNumber, (byte) 2, reason, e.getUnbannedUser(), entry.getAuthor(), msg);
				});
			}
		});
	}

	@Override
	public void GuildMemberRemove(GuildMemberRemoveEvent e) {
		if (e.getGuild().getID() != Main.config.modLogs.guildID) {
			return;
		}
		CompletableFuture<IAuditLog> cf = e.getGuild().getAuditLog(ActionTypes.MEMBER_KICK, 1);
		cf.thenAcceptAsync(aLogs -> {
			if (aLogs.getEntries().size() > 0) {
				IAuditLogEntry entry = aLogs.getEntries().get(0);
				if (entry.getTargetID() != e.getMember().getID() || System.currentTimeMillis() - ((entry.getID() >> 22) + SnowflakeUtil.DISCORD_EPOCH) > 2000l) { // make sure that the member was actually kicked
					return; // and return early if they weren't or if this is an old entry
				}
				long caseNumber = getNextCaseNumber();
				final String reason = entry.getReason() == null ? getReasonText(caseNumber) : entry.getReason();
				RichEmbed embed = new RichEmbed().setAuthor("Member Kicked", "", aLogs.getUsers().get(entry.getTargetID()).getAvatar().toString());
				embed.setColor(0x77a3ea).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d) (%s)", e.getMember(), e.getMember().getID(), e.getMember().toMention()));
				embed.addField("Reason", reason);
				embed.addField("Responsible Moderator", entry.getAuthor());
				e.getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID).sendEmbed(embed).thenAcceptAsync(msg -> {
					DataBase.createCase(caseNumber, (byte) 1, reason, e.getMember().getUser(), entry.getAuthor(), msg);
				});
			}
		});
		cf.exceptionally(ex -> {
			ex.printStackTrace();
			return null;
		});
	}

}
