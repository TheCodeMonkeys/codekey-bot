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
				System.out.println("hmm");
				try {
					String rolesTXT = "Roles:\n";
					for (IRole role : roles.values()) {
						if (rolesTXT.length() + String.format("Name: %s, Position: %d, Permissions: %d, Color: %d, Hoisted: %b, Mentionable: %b, ID: %d, Managed: %b\n", role.getName().equals("@everyone") ? "everyone" : role.getName(), role.getPosition(), role.getPermissions().toLong(), role.getColor(), role.isHoisted(), role.isMentionable(), role.getID(), role.isManaged()).length() < 2000)
							rolesTXT += String.format("Name: %s, Position: %d, Permissions: %d, Color: %d, Hoisted: %b, Mentionable: %b, ID: %d, Managed: %b\n", role.getName().equals("@everyone") ? "everyone" : role.getName(), role.getPosition(), role.getPermissions().toLong(), role.getColor(), role.isHoisted(), role.isMentionable(), role.getID(), role.isManaged());
					}
					System.out.println(rolesTXT.length());
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
			// future.completeExceptionally(ex)
		}
	}
}
