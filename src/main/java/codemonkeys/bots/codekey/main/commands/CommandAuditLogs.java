package codemonkeys.bots.codekey.main.commands;

import java.util.concurrent.CompletableFuture;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.entity.auditlog.IAuditLog;

/**
 * @author Perry Berman
 *
 */
public class CommandAuditLogs extends Command {
	
	/**
	 * 
	 */
	public CommandAuditLogs() {
		setUnlocalizedName("auditlogs");
	}
	
	@Override
	public void execute(MessageCreateEvent e, String[] args) throws Exception {
		System.out.println("Get this");
		if (e.getMessage().getGuild() != null && e.getMessage().getGuild().getID() == Main.config.modLogs.guildID) {
			CompletableFuture<IAuditLog> cf = e.getMessage().getGuild().getAuditLog();
			IAuditLog aLog = cf.get();
			System.out.println(aLog.getEntries().size());
		}
	}
}
