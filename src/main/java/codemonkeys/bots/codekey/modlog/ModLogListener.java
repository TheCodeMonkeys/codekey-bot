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

/**
 * @author Perry Berman
 *
 */
public class ModLogListener extends EventListenerAdapter {

	private long getNextCaseNumber() {
		return DataBase.getLatestCaseNumber() + 1;
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
			if (aLogs.getEntries().size() > 0) {
				IAuditLogEntry entry = aLogs.getEntries().get(0);
				long caseNumber = getNextCaseNumber();
				final String reason = entry.getReason() == null ? String.format("No reason provided. Use `!reason <%d> <reason>` to change the reason.", caseNumber) : entry.getReason();
				RichEmbed embed = new RichEmbed().setAuthor("Member Banned", "", e.getBannedUser().getAvatar().toString());
				embed.setColor(0xd0021b).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d)", e.getBannedUser(), e.getBannedUser().getID()));
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
			if (aLogs.getEntries().size() > 0) {
				IAuditLogEntry entry = aLogs.getEntries().get(0);
				long caseNumber = getNextCaseNumber();
				final String reason = entry.getReason() == null ? String.format("No reason provided. Use `!reason <%d> <reason>` to change the reason.", caseNumber) : entry.getReason();
				RichEmbed embed = new RichEmbed().setAuthor("Member Unbanned", "", e.getUnbannedUser().getAvatar().toString());
				System.out.println(e.getUnbannedUser().getAvatar());
				embed.setColor(0x00ff00).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d)", e.getUnbannedUser(), e.getUnbannedUser().getID()));
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
				System.out.println(entry.getTargetID());
				if (entry.getTargetID() != e.getMember().getID()) { // make sure that the member was actually kicked
					return; // and return early if they weren't
				}
				long caseNumber = getNextCaseNumber();
				System.out.println(entry.getReason());
				final String reason = entry.getReason() == null ? String.format("No reason provided. Use `!reason %d <reason>` to change the reason.", caseNumber) : entry.getReason();
				RichEmbed embed = new RichEmbed().setAuthor("Member Kicked", "", e.getMember().getUser().getAvatar().toString());
				embed.setColor(0x77a3ea).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d) (%s)", e.getMember(), e.getMember().getID(), e.getMember().asMention()));
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
