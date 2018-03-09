package codemonkeys.bots.codekey.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import codemonkeys.bots.codekey.level.Player;
import codemonkeys.bots.codekey.level.PlayerUtils;
import io.discloader.discloader.common.event.DisconnectEvent;
import io.discloader.discloader.common.event.EventListenerAdapter;
import io.discloader.discloader.common.event.ReadyEvent;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.common.registry.EntityRegistry;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Listener extends EventListenerAdapter {
	/**
	 * Map of the last time a user was given EXP indexed by the user's
	 * {@link IUser#getID() id}
	 */
	public static Map<Long, Long> lastEXP = new HashMap<>();

	@Override
	public void GuildMessageCreate(GuildMessageCreateEvent e) {
		if (e.getGuild().getID() != 201544496654057472l)
			return;
		IMessage message = e.getMessage();
		IUser author = message.getAuthor();
		long id = author.getID();

		/*
		 * If the message starts with PREFIX (~) then it will be further checked for
		 * word 'status'
		 * 
		 * if (event.getMessage().getContent().startsWith(Main.PREFIX))
		 * handleCommand(event.getMessage().getContent(), event);
		 * 
		 * If the user is a bot OR they are in spam timer OR its the spam channel then
		 * ignore the message
		 */
		if (e.getMessage().getAuthor().isBot() || e.getChannel().getID() == 208003522157871124l)
			return;
		if (e.getMessage().getContent().toLowerCase().startsWith(Main.PREFIX + "status")) // don't give exp if the user is checking someones status
			return;
		long currentTime = System.currentTimeMillis();
		Player p = Main.players.get(id);
		// create new a player entry if an entry wasn't found in the players list.
		if (p == null) {
			// Fired if a person's ID doesn't already exist in my list. if so, then make a
			// new entry and Add points to that person according to their role.
			p = PlayerUtils.addNewPlayerEntryWithRank(id, e.getMessage().getMember().getRoles());
			Main.logger.info("Registering new user (" + author.toString() + ") into the database");
		}
		p.setLastMsgID(e.getMessage().getID());
		if (lastEXP.containsKey(id) && currentTime - lastEXP.get(id) < 60000) // if it's been less than a minute since the user received EXP return.
			return;

		/*
		 * If the program gets here, either the user's cooldown is over or, they haven't
		 * sent a message . So we should set the value at user.id in the lastEXP map to
		 * currentTime
		 */
		lastEXP.put(id, currentTime); // id is the user's ID, and currentTime is the current time in milliseconds.

		// Adds exp to respective player with the formula EXP=WORDS/TOTAL_LENGTH with
		// some adjustments

		double exp = PlayerUtils.getEXPFromMessage(message.getContent());
		p.addExp(exp, e);

		Main.logger.info("Gave " + author + " " + exp + "EXP");
		try {
			// Once player gets the new score, update the database file.
			writeToJSON();
		} catch (Exception ex) {
			System.out.println("Unable to write file... Dming the creator");
			ex.printStackTrace();
			EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());

		}
	}

	protected void writeToCSV() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(Main.DATABASE));
		List<Player> players = new ArrayList<>(Main.players.values());
		for (int i = 0; i < players.size(); i++) {
			writer.write(players.get(i).getID() + "," + players.get(i).getExp());
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	public static void writeToJSON() throws IOException {
		FileWriter fw = new FileWriter(Main.DATABASE);
		JSONObject json = new JSONObject(), playerJSON = new JSONObject(); // cache the playerJSON object instead of creating a new one for each player
		Main.players.forEach((id, player) -> {
			playerJSON.put("lastMsgID", player.getLastMsgID()).put("exp", player.getExp());
			json.put(Long.toUnsignedString(id, 10), playerJSON);
		});
		fw.write(json.toString(4));
		fw.close();
	}

	public void Ready(ReadyEvent e) {
		Main.logger.info("Codekey is now ready to communicate with Discord");
	}

	@Override
	public void Disconnected(DisconnectEvent e) {
		Main.logger.severe("Got disconnected from the gateway");
		// if (e.getClientFrame().getCloseCode() == 1007 ||
		// e.getServerFrame().getCloseCode() == 1007) {
		// System.exit(1);
		// }
	}

}
