package codekey.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codekey.level.Player;
import codekey.level.PlayerUtils;
import io.discloader.discloader.common.event.EventListenerAdapter;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.common.registry.EntityRegistry;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Listener extends EventListenerAdapter {

	public static ArrayList<IUser> lastMessage = new ArrayList<>();
	public static Map<Long, Long> lastEXP = new HashMap<>();

	@Override
	public void GuildMessageCreate(GuildMessageCreateEvent e) {
		IMessage message = e.getMessage();
		IUser author = message.getAuthor();
		long id = author.getID();

		if (!PlayerUtils.listContainsId(id) && !author.isBot()) {
			// Fired if a person's ID doesn't already exist in my list. if so, then make a
			// new entry and Add points to that person according to their role.
			PlayerUtils.addNewPlayerEntryWithRank(id, e.getMessage().getMember().getRoles());
			Main.logger.info("Registering new user (" + author.toString() + ") into the database");
		}

		// If the message starts with PREFIX (~) then it will be further checked for
		// word 'status'
		// if (event.getMessage().getContent().startsWith(Main.PREFIX))
		// handleCommand(event.getMessage().getContent(), event);

		// If the user is a bot OR they are in spam timer OR its the spam channel then
		// ignore the message
		if (e.getMessage().getAuthor().isBot() || e.getChannel().getID() == 208003522157871124l)
			return;
		if (e.getMessage().getContent().toLowerCase().startsWith(Main.PREFIX + "status")) // don't give exp if the user is checking someones status
			return;
		long currentTime = System.currentTimeMillis();
		if (lastEXP.containsKey(id) && currentTime - lastEXP.get(id) < 60000) // if it's been less than a minute since the user received EXP return.
			return;

		/*
		 * If the program gets here, either the user's cooldown is over or, they haven't
		 * sent a message . So we should set the value at user.id in the lastEXP map to
		 * currentTime
		 */
		lastEXP.put(id, currentTime); // id is the user's ID, and currentTime is the current time in milliseconds.

		// If code reaches here then it means that the user is eligible to get points.
		// but before they get their sweet EXP code adds them to a spam counter so they
		// wont
		// get any more EXP for the next 5 mins or so based on the time given in
		// SpamThread.java
		// lastMessage.add(author);

		// Adds exp to respective player with the formula EXP=WORDS/TOTAL_LENGTH with
		// some adjustments
		Player p = Main.players.get(id);
		if (p != null) {
			double exp = PlayerUtils.getEXPFromMessage(message.getContent());
			p.addExp(exp, e);
			Main.logger.info("Gave " + author + " " + exp + "EXP");
		}
		try {
			// Once player gets the new score, update the database file.
			writeToCSV();
		} catch (Exception ex) {
			System.out.println("Unable to write file... Dming the creator");
			ex.printStackTrace();
			EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());

			// e.getGuild().getMemberById("201723870863032321").getUser().openPrivateChannel().queue(Channel
			// -> {
			// Channel.sendMessage("error in bot " + ex.getMessage()).queue();
			// });
		}
	}

	// private boolean lastMessageBySamePerson(User user) {
	// return user.equals(lastMessage);
	// }

	private void writeToCSV() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(Main.DATABASE));
		List<Player> players = new ArrayList<>(Main.players.values());
		for (int i = 0; i < players.size(); i++) {
			writer.write(players.get(i).getID() + "," + players.get(i).getExp());
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	// private void handleCommand(String msg, GuildMessageReceivedEvent event) {
	//
	// if (event.getMessage().getMentionedUsers().size() > 0 &&
	// msg.contains("status")) {
	// String id = event.getMessage().getMentionedUsers().get(0).getId();
	//
	// for (Player p : Main.players) {
	// if (p.getId().equals(id)) {
	// event.getChannel().sendMessage("Present Rank : " +
	// PlayerUtils.getRankFromExp(p.getExp()) + "\n" + "Present EXP : " + p.getExp()
	// + "\n" + "Next Rank : " +
	// PlayerUtils.getNextRank(PlayerUtils.getRankFromExp(p.getExp())) + "\n" + "EXP
	// Needed : " + PlayerUtils.expNeededForNextRank(p.getExp())).queue();
	// break;
	// }
	//
	// }
	//
	// } else if (msg.contains("status")) {
	// String id = event.getAuthor().getId();
	//
	// for (Player p : Main.players) {
	// if (p.getId().equals(id)) {
	// event.getChannel().sendMessage("Present Rank : " +
	// PlayerUtils.getRankFromExp(p.getExp()) + "\n" + "Present EXP : " +
	// (Math.floor(p.getExp())) + "\n" + "Next Rank : " +
	// PlayerUtils.getNextRank(PlayerUtils.getRankFromExp(p.getExp())) + "\n" + "EXP
	// Needed : " +
	// Math.floor(PlayerUtils.expNeededForNextRank(p.getExp()))).queue();
	// break;
	// }
	//
	// }
	// }
	// }

}
