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
import io.discloader.discloader.client.command.CommandHandler;
import io.discloader.discloader.common.event.DisconnectEvent;
import io.discloader.discloader.common.event.EventListenerAdapter;
import io.discloader.discloader.common.event.RawEvent;
import io.discloader.discloader.common.event.ReadyEvent;
import io.discloader.discloader.common.event.guild.member.GuildMemberRoleAddEvent;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.common.registry.EntityRegistry;
import io.discloader.discloader.core.entity.RichEmbed;
import io.discloader.discloader.entity.auditlog.ActionTypes;
import io.discloader.discloader.entity.auditlog.AuditLogChangeKeys;
import io.discloader.discloader.entity.auditlog.IAuditLogEntry;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

/**
 * Created by Thvardhan and maintained by R3alCl0ud from codemonkeys discord
 * server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Listener extends EventListenerAdapter {
	/**
	 * Map of the last time a user was given EXP indexed by the user's
	 * {@link IUser#getID() id}
	 */
	public static Map<Long, Long> lastEXP = new HashMap<>();

	public static void writeToJSON() throws IOException {
		Main.logger.info("Saving Player Data");
		FileWriter fw = new FileWriter(Main.DATABASE);
		JSONObject json = new JSONObject(); // cache the playerJSON object instead of creating a new one for each player
		Main.players.forEach((id, player) -> {
			JSONObject playerJSON = new JSONObject().put("lastMsgID", player.getLastMsgID()).put("exp", player.getExp());
			json.put(Long.toUnsignedString(id, 10), playerJSON);
		});
		fw.write(json.toString(4));
		fw.close();
	}

	@Override
	public void Disconnected(DisconnectEvent e) {
		Main.logger.severe("Got disconnected from the gateway");
		// System.exit(0);
	}

	@Override
	public void GuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
		if (e.getRole().getID() == 295918777613287444l) {
			System.out.println("Does this work?");
			e.getGuild().getAuditLog(ActionTypes.MEMBER_ROLE_UPDATE, 1).thenAcceptAsync(aLogs -> {
				try {
					if (aLogs.getEntries().isEmpty())
						return;
					IAuditLogEntry entry = aLogs.getEntries().get(0);
					for (int i = 0; i < entry.getChanges().size(); i++) {
						if (entry.getChanges().get(i).getKey() == AuditLogChangeKeys.$ADD) {
							List<Object> values = entry.getChanges().get(i).<ArrayList<Object>>getNewValue();
							for (int n = 0; n < values.size(); n++) {
								JSONObject value = (JSONObject) JSONObject.wrap(values.get(n));
								if (Long.parseUnsignedLong(value.get("id").toString(), 10) == 295918777613287444l) {
									RichEmbed embed = new RichEmbed("Theres a new Trashlord in Town").setColor(0x7384ce);
									embed.addField("The Trashlord", e.getMember() + " (" + e.getMember().getID() + ") (" + e.getMember().toMention() + ")", true);
									embed.addField("Deciding Moderator", entry.getAuthor(), true);
									embed.setTimestamp();
									e.getGuild().getTextChannelByID(203243917737459716l).sendEmbed(embed);
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	@Override
	public void GuildMessageCreate(GuildMessageCreateEvent e) {
		if (e.getGuild().getID() != 201544496654057472l || Main.players == null) {
			return;
		}
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
		 * If the user is a bot OR they are checking their status ignore them
		 */
		if (e.getMessage().getAuthor().isBot() || e.getMessage().getContent().toLowerCase().startsWith(Main.PREFIX + "status")) // don't give exp if the user is checking someones status
			return;
		long currentTime = System.currentTimeMillis();
		Player player = Main.players.get(id);
		// create new a player entry if an entry wasn't found in the players list.
		if (player == null) {
			// Fired if a person's ID doesn't already exist in my list. if so, then make a
			// new entry and Add points to that person according to their role.
			player = PlayerUtils.addNewPlayerEntryWithRank(id, e.getMessage().getMember().getRoles());
			Main.logger.info("Registering new user (" + author.toString() + ") into the database");
		}
		player.setLastMsgID(e.getMessage().getID());

		player.checkForNewRank(e); // always check rank to force syncing

		if ((lastEXP.containsKey(id) && currentTime - lastEXP.get(id) < 60000) || e.getChannel().getID() == 208003522157871124l) {
			// if it's been less than a minute since the user received EXP, write to JSON
			// and return.
			try {

				// Once player gets the new score, update the database file.
				DataBase.savePlayer(e.getGuild(), player);
				writeToJSON();
			} catch (Exception ex) {
				System.out.println("Unable to save player data... Dming the creator");
				ex.printStackTrace();
				EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());
			}
			return;
		}

		/*
		 * If the program gets here, either the user's cooldown is over or, they haven't
		 * sent a message . So we should set the value at user.id in the lastEXP map to
		 * currentTime
		 */
		lastEXP.put(id, currentTime); // id is the user's ID, and currentTime is the current time in milliseconds.

		// Adds exp to respective player with the formula EXP=WORDS/TOTAL_LENGTH with
		// some adjustments

		double exp = PlayerUtils.getEXPFromMessage(message.getContent());
		player.addExp(exp, e);

		Main.logger.info("Gave " + author + " " + exp + "EXP");
		try {
			// Once player gets the new score, update the database file.
			DataBase.savePlayer(e.getGuild(), player);
			writeToJSON();
		} catch (Exception ex) {
			System.out.println("Unable to save player data... Dming the creator");
			ex.printStackTrace();
			EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());
		}
	}

	@Override
	public void Ready(ReadyEvent e) {
		Main.logger.info("Codekey is now ready to communicate with Discord");
		Main.logger.info("Connecting to the DataBase if not already connected");
		e.getLoader().getSelfUser().setGame(CommandHandler.prefix + "help || " + CommandHandler.prefix + "status");
		DataBase.connect();
		IGuild guild = Main.getGuild();
		if (guild != null) {
			guild.fetchRoles();
			guild.fetchMembers().thenAccept(members -> {
				if (Main.players == null || Main.players.size() == 0) {
					Main.logger.info("Loading Player Data");
					DataBase.loadPlayers(guild);
					Main.logger.info("Loaded " + Main.players.size() + " Player(s)");
				} else if (Main.players != null) {
					DataBase.savePlayers(guild);
				}
			});
		}
	}

	@Override
	public void RawPacket(RawEvent e) {
		if (e.isGateway()) {
			if (e.getFrame().getPayloadText().contains("GUILD_BAN") || e.getFrame().getPayloadText().contains("MESSAGE_CREATE") || (e.getFrame().getPayloadText().contains("GUILD_MEMBER") && !e.getFrame().getPayloadText().contains("GUILD_MEMBERS_CHUNK"))) {
				new Thread(() -> {
//					System.out.println(e.getFrame().getPayloadText());
				}).start();
			}
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

}
