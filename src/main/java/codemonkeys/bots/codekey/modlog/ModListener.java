package codemonkeys.bots.codekey.modlog;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.common.event.EventListenerAdapter;
import io.discloader.discloader.common.event.ReadyEvent;
import io.discloader.discloader.common.event.guild.GuildBanAddEvent;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.auditlog.ActionTypes;
import io.discloader.discloader.entity.auditlog.IAuditLogEntry;

/**
 * @author Perry Berman
 *
 */
public class ModListener extends EventListenerAdapter {
	
	protected long caseNumber = 0l;
	
	@Override
	public void Ready(ReadyEvent e) {
		DbManager.connect();
		caseNumber = DbManager.getLatestCaseNumber();
	}
	
	@Override
	public void GuildBanAdd(GuildBanAddEvent e) {
		if (e.getGuild().getID() != Main.config.modLogs.guildID)
			return;
		e.getGuild().getAuditLog(ActionTypes.MEMBER_BAN_ADD, 1).thenAcceptAsync(aLogs -> {
			if (aLogs.getEntries().size() > 1) {
				IAuditLogEntry entry = aLogs.getEntries().get(0);
				caseNumber++;
				String reason = entry.getReason();
				if (reason == null) {
					reason = String.format("No reason provided. Use `!reason <%d> <reason>` to change the reason.", caseNumber);
				}
				RichEmbed embed = new RichEmbed("Member Banned").setAuthor(entry.getAuthor().toString(), "", entry.getAuthor().getAvatar().toString());
				embed.setColor(0xd0021b).setFooter("Case #" + caseNumber).setTimestamp();
				embed.addField("Member", String.format("%s (%d)", e.getBannedUser(), e.getBannedUser().getID()));
				e.getGuild().getTextChannelByID(Main.config.modLogs.logsChannelID).sendEmbed(embed).thenAcceptAsync(msg -> {
					DbManager.createCase(caseNumber, e.getBannedUser(), entry.getAuthor(), msg);
				});
			}
		});
	}
	
}
