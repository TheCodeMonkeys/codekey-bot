package codemonkeys.bots.codekey.main.commands;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.discloader.discloader.client.command.Command;
import io.discloader.discloader.common.event.message.MessageCreateEvent;
import io.discloader.discloader.entity.guild.IRole;

/**
 * @author Perry Berman
 *
 */
public class CommandFetchRoles extends Command {

	public CommandFetchRoles() {
		setUnlocalizedName("fetchroles");
	}

	@Override
	public void execute(MessageCreateEvent e, String[] args) throws Exception {
		if (e.getMessage().getGuild() != null) {
			CompletableFuture<Map<Long, IRole>> future = e.getMessage().getGuild().fetchRoles();
			future.thenAcceptAsync(roles -> {
				try {
					String rolesTXT = "Roles:\n";
					for (IRole role : roles.values()) {
						if (rolesTXT.length() + String.format("Name: %s, ID: %d\n", role.getName().equals("@everyone") ? "everyone" : role.getName(), role.getID()).length() < 2000) {
							rolesTXT += String.format("Name: %s, ID: %d\n", role.getName().equals("@everyone") ? "everyone" : role.getName(), role.getID());
						} else {
							break;
						}
					}
					e.getChannel().sendMessage(rolesTXT).get();
				} catch (Exception e1) {
					e1.printStackTrace();
					e.getChannel().sendMessage("Error: " + e1.getMessage());
				}
			});
			future.exceptionally(ex -> {
				ex.printStackTrace();
				e.getChannel().sendMessage("Error: " + ex.getMessage());
				return null;
			});
		}
	}
}
